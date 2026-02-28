import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router'
import { PostService } from '../../services/postService'
import { PostResponse } from '../../models/postResponse';
import { CommonModule, DatePipe } from '@angular/common';
import { CommentList } from '../../Components/comment-list/comment-list';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment'
import { ConfirmReport } from '../../Components/confirm-report/confirm-report';
import { Report } from '../../services/report'
import { MatDialog } from '@angular/material/dialog';
import { Token } from '../../services/token';
import { confirmAction } from '../../Components/confirm-action/confirm-action'
import { ToastService } from '../../services/ToastService';
import { DeletePostBtn } from '../../Components/delete-post-btn/delete-post-btn';

@Component({
  selector: 'app-post',
  templateUrl: './post.html',
  imports: [CommonModule, RouterLink, DatePipe, CommentList, confirmAction, ConfirmReport, DeletePostBtn],
  styleUrl: './post.css',
})

export class Post implements OnInit {
  baseUrl = environment.apiUrl

  post: PostResponse | null = null
  loading: boolean = true
  totalLikes: number = 0
  confirmErrMsg: String = ''
  isOwnPost: boolean = false
  action_msg: string = ''
  postIdToDelete: number = -1

  liked = false
  isAdmin = false

  report = {
    reportProfile: false,
    reportPost: false,
    reason: ""
  }

  //
  currentSlideIndex: number = 0;

  // Method to move to the next slide
  nextSlide() {
    if (this.post && this.post.media.length > 0) {
      this.currentSlideIndex = (this.currentSlideIndex + 1) % this.post.media.length;
    }
  }

  // Method to move to the previous slide
  prevSlide() {
    if (this.post && this.post.media.length > 0) {
      // Use (n + length) % length to handle negative results cleanly
      this.currentSlideIndex = (this.currentSlideIndex - 1 + this.post.media.length) % this.post.media.length;
    }
  }

  constructor(private route: ActivatedRoute, private toast: ToastService, private postService: PostService,
    private http: HttpClient,
    private reportService: Report,
    private router: Router, 
    private token: Token
  ) { }

  dialogConfig = {
    title: '',
    content: ''
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(
      params => {
        const postId = params.get("id")

        if (postId) {
          this.fetchPost(+postId)
        } else {
          this.loading = false
        }
      }
    )
  }

  //get posts
  fetchPost(id: number): void {
    this.loading = true
    // set  the post id for the report request
    this.reportRequest.reportedPostId = id
    this.postService.getPost(id).subscribe({
      next: data => {
        this.post = data
        this.postIdToDelete = data.id
        this.currentSlideIndex = 0
        this.totalLikes = data.totalLikes
        this.liked = data.likedByCurrentUser
        this.isOwnPost = data.authorUsername == this.token.getUsername()
        this.isAdmin = this.token.getRole() === 'ADMIN'
        this.loading = false
      },
      error: error => {
        this.loading = false

        this.router.navigate(['/home'])
        this.toast.show('failed to load post')

      }
    })
  }

  // post likes
  toggleLikes() {
    this.http.post(`${this.baseUrl}/likes/${this.post?.id}`, {}, { responseType: "text" })
      .subscribe({
        next: () => {
          this.liked = !this.liked;
          this.totalLikes += this.liked ? 1 : -1
        },
        error: (err) => {
          console.log("sometihng wrong");
          //alert("like process failed")
        }
      })
  }
 
  reportRequest = {
    reportedPostId: 0,
    reason: ''
  }

  hideConfirm(): void {
    this.report.reportPost = false
    this.report.reason = ''
    this.confirmErrMsg = ''
  }

  // report post handler
  submitReport() {
    this.reportRequest.reason = this.report.reason;
    this.loading = true;

    this.reportService.submitReport(this.reportRequest).subscribe({
      next: () => {
        this.hideConfirm()
        this.loading = false
        this.toast.show('reported successfully')
      },
      error: err => {
        console.log(err);
        
        this.confirmErrMsg = err.error.detail
        this.loading = false
      },
      complete: () => {
        this.reportRequest.reason = ''
      }

    })

  }

  deletePost() {
    this.action_msg = 'delete post?'
  }


  confirmDeletePostAction() {
    if (this.postIdToDelete > 0) {
      this.postService.deletePost(this.postIdToDelete).subscribe({
        next: () => {
          this.action_msg = ''
          this.postIdToDelete = -1

          this.toast.show('post deleted successfully', 'delete post')
          this.router.navigate(['/home'])
        },
        error: (err) => {
          this.toast.show(err.error.detail, 'failed to delete post')

        }
      })
    }
  }

  cancelDeletePostAction() {
    this.action_msg = ''
  }

  editPost() {
    this.router.navigate(['/edit-post', this.post?.id])
  }

}
