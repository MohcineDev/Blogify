import { CommonModule, ViewportScroller } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Token } from '../../services/token';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../services/ToastService';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})

export class Home implements OnInit {

  baseUrl = `${environment.apiUrl}/posts`
  posts: any[] = []
  page = 0;
  size = 5
  endOfList = false
  loading = false

  hideLoadBtn: boolean = false
  hideLessBtn: boolean = false

  constructor(private http: HttpClient, public tokenService: Token, private toast: ToastService) { }


  ngOnInit(): void {
    this.getPosts()
  }

  getPosts() {
    if (this.loading || this.endOfList) {
      return
    }

    this.loading = true
    this.http.get<any[]>(`${this.baseUrl}/feed?page=${this.page}&size=${this.size}`)
      .subscribe({
        next: (res) => {
          if (res.length === 0) {
            this.endOfList = true
          } else {
            this.posts = res
          }

          this.hideLoadBtn = res.length < this.size ? true : false
          this.hideLessBtn = this.page <= 0 ? true : false

          window.scroll({
            behavior: 'smooth',
            top: 0
          })
          this.loading = false
        },
        error: (err) => {
          if (err.status != 401) {
            
            this.toast.show('', 'fetching posts failed')
            this.loading = false
          }
        }
      })
  }

  loadmore() {
    this.page++;
    this.getPosts()
  }

  loadless() {
    this.page--;
    this.getPosts()
  }
} 