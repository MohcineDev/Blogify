import { HttpClient } from '@angular/common/http';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../../services/ToastService';

@Component({
  selector: 'app-comment-form',
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './comment-form.html',
  standalone: true,
  styleUrl: './comment-form.css',
})
export class CommentFormComponent {

  @Input() postId!: number;
  @Output() commentAdded = new EventEmitter<any>(); // Emits the new comment data

  baseUrl = environment.apiUrl
  newComment = ''
  commentForm: FormGroup;
  isSubmitting: boolean = false;
  submitError: string | null = null;
  dialogTitle = ''

  //custom validator
  noWhiteSpaceValidator(conrtol: AbstractControl): ValidationErrors | null {
    const value = conrtol.value || ''
    if (value.length > 1) {

      const isWhiteSpace = value.trim().length < 2
      const isValid = !isWhiteSpace
      return isValid ? null : { 'whitespace': true }
    } else
      return null
  }

  constructor(private http: HttpClient,
    private fb: FormBuilder, private toast: ToastService,
    private dialog: MatDialog,

    private router: Router) {

    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(2), this.noWhiteSpaceValidator, Validators.maxLength(100)]]

    });
  }

  //getter
  get contentControl() {
    return this.commentForm.get('content')
  }


  addComment(): void {
    if (this.commentForm.invalid) {
      return;
    }

    if (this.commentForm.invalid || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.submitError = null;


    const dto = {
      content: this.commentForm.value.content.trim()
    };

    this.http.post(`${this.baseUrl}/comments/${this.postId}`,
      dto
    ).subscribe({
      next: (newComment) => {
        this.commentAdded.emit(newComment); // Notify parent (CommentListComponent)
        this.commentForm.reset(); // Clear the form
        this.isSubmitting = false;
      },
      error: (err) => {

        if (err.status == 404) {
          this.router.navigate(['/404'])
        } else {
          if (err.status == 401) {

            this.toast.show('alert', err.error.detail)
            this.submitError = 'Failed to post comment. Please try again.';
            this.isSubmitting = false;
          }
        }
      }
    });
  }
}

