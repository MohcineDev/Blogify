import { Component, OnInit, Input } from '@angular/core';
import { CommentResponse } from '../../models/commentResponse'; // Define this interface
import { CommentFormComponent } from '../comment-form/comment-form'
import { DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Token } from '../../services/token';
import { ToastService } from '../../services/ToastService';
import { confirmAction } from '../confirm-action/confirm-action';

@Component({
  selector: 'app-comment-list',
  imports: [CommentFormComponent, DatePipe, RouterLink, confirmAction],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.css',
})

export class CommentList implements OnInit {
  baseUrl = "http://localhost:8080/api"

  @Input() postId!: number; // Input property to receive the post ID

  comments: CommentResponse[] = [];
  loading: boolean = true;
  error: string | null = null;
  loggedUser: string | null = null
  action_msg: string = ''

  constructor(private http: HttpClient, private token: Token, private toast: ToastService, private router: Router) { }

  dialogConfig = {
    title: '',
    content: ''
  }


  ngOnInit(): void {
    if (this.postId) {
      this.loggedUser = this.token.getUsername()
      this.fetchComments();
    }
  }

  fetchComments(): void {
    this.loading = true;
    this.http.get(`${this.baseUrl}/comments/${this.postId}`).subscribe({
      next: (data: any) => {
        this.comments = data;

        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching comments:', err);
        this.error = 'Failed to load comments.';
        this.loading = false;
      }
    });
  }

  // This method is called by the CommentFormComponent when a new comment is adddded
  onCommentAdded(newComment: CommentResponse): void {
    this.comments.unshift(newComment); // Add the new comment to the top of the list
  }
  commentToDelete: number | null = null
  deleteComment(id: number) {
    this.action_msg = 'delete Comment ?'
    this.commentToDelete = id;
  }

  confirmDeletePostAction() {

    this.http.delete(`${this.baseUrl}/comments/${this.commentToDelete}`).subscribe({
      next: () => {
        this.fetchComments();
        this.action_msg = ''

      },
      error: (err) => {
        this.action_msg = ''
        if (err.status == 404) {
          this.toast.show(err.error.detail)

        } else {
          this.toast.show('only owner can delete it!')

        }
      }
    })
  }

  cancelDeletePostAction() {
    this.action_msg = ''
  }
}