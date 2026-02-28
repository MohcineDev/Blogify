import { HttpClient } from '@angular/common/http';
import { booleanAttribute, Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment'
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { PostService } from '../../services/postService';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../../services/ToastService';

// editor imports 

interface previewFile {
  file: File,
  url: string,
  isImage: boolean,
  isUploading: boolean,
  uploadedUrl: string | null /// url from spring after upload
}

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './create-post.html',
  styleUrl: './create-post.css',
})

export class CreatePost implements OnInit {

  postId: number | null = null
  isEditMode = false

  baseUrl = `${environment.apiUrl}/posts`
  selectedFiles: previewFile[] = []
  loading = false
  myForm!: FormGroup


  constructor(private fb: FormBuilder, private toast: ToastService, private http: HttpClient, private route: ActivatedRoute, private router: Router) {
    this.myForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      content: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]]
    })
  }
  dialogConfig = {
    title: '',
    content: ''
  }
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')

    if (id) {
      this.postId = +id
      this.isEditMode = true
      this.loadPostData(this.postId)
    }

  }

  // in the editing mode
  loadPostData(id: number) {
    this.loading = true

    this.http.get<any>(`${this.baseUrl}/${id}`)
      .subscribe({
        next: (post) => {
          this.myForm.patchValue({
            title: post.title,
            content: post.content
          })

          // load media
          this.selectedFiles = post.media.map((elem: any) => ({
            url: elem.url,
            isImage: elem.type === 'IMAGE',
            uploadedUrl: elem.url

          }))
          this.loading = false

        },
        error: (err) => {
          if (err.status === 404) {
            this.router.navigate(['/404']);
          }
          this.loading = false

        }
      })
  }

  allowedImgs = ["jpg", "jpeg", "png", "webp", "avif"];
  allowedVideos = ["mp4"];

  onFileSelected(event: any) {
    const files: FileList = event.target.files
    const MAX_SIZE = 1024 * 1024 * 20 // 20megabyte

    if (files) {
      if (this.selectedFiles.length + files.length > 5) {
        alert("the max files is 5")
        return
      }
      let totalSize = 0
      //check file extension
      for (let i = 0; i < files.length; i++) {
        let fileName = files[i].name
        let ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
        let allowedImg = this.allowedImgs.includes(ext)
        let allowedVideo = this.allowedVideos.includes(ext)

        // if (!allowedImg && !allowedVideo) {

        //   this.toast.show('unsuporrted ext')
        //   return
        // }
        if (allowedImg && !files[i].type.startsWith('image/')) {
          this.toast.show('unsuporrted file')

          return
        }
        if (allowedVideo && !files[i].type.startsWith('video/')) {
          this.toast.show('unsuporrted file')
          return
        }
      }

      //this.selectedFiles.forEach(elem => totalSize += elem.file.size)

      for (let i = 0; i < files.length; i++) {
        totalSize += files[i].size
      }

      if (totalSize > MAX_SIZE) {
        alert("you exeeded the max size 20MB")
        return
      }

      for (let i = 0; i < files.length; i++) {
        const file = files[i]

        const previewUrl = URL.createObjectURL(file)
        const isImage = file.type.startsWith('image/')

        this.selectedFiles.push(
          {
            file: file,
            url: previewUrl,
            isImage: isImage,
            isUploading: false,
            uploadedUrl: null
          }
        )
      }
    }

  }


  //CREATE POST
  async createPost() {
    if (this.myForm.invalid) {
      return
    }

    this.loading = true
    const formData = new FormData()

    const existingMedia = this.selectedFiles.filter(f => !f.file)
      .map(f => f.url)

    const postData = {
      title: this.myForm.value.title,
      content: this.myForm.value.content,
      keptMediaUrls: existingMedia
    }

    formData.append('post', new Blob([JSON.stringify(postData)], {
      type: 'application/json'
    }))

    //append new files they have "file" property
    this.selectedFiles.forEach(elem => {
      if (elem.file) {
        formData.append('files', elem.file)
      }
    })

    const url = this.isEditMode ? `${this.baseUrl}/${this.postId}` : this.baseUrl

    const req = this.isEditMode ? this.http.put(url, formData) : this.http.post(url, formData)


    req.subscribe({
      next: (res: any) => {
        this.myForm.reset()
        this.loading = false
        this.selectedFiles = []
        let msg = this.isEditMode ? 'updated' : 'created'

        this.toast.show('', `post ${msg} Successfully`)

        this.router.navigate(['/post', res.id])

        // redirect to home
      }, error: (err) => {
        if (err.status === 404) {
          this.router.navigate(['/404']);
        }

        this.toast.show(err.error.detail, 'alert')
        this.loading = false
      }
    })
  }


  //------- helper functions
  checkIfImage(file: File): boolean {
    return file.type.startsWith('image/')
  }
  // remove
  removeFile(i: number) {
    this.selectedFiles.splice(i, 1)
  }
}
