import { HttpClient } from '@angular/common/http';
import { Component, ChangeDetectionStrategy, OnInit, signal, DestroyRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Token } from '../../services/token';
import { User } from '../../models/user';
import { MatButtonModule } from '@angular/material/button';
import { SubscriptionService } from '../../services/subscription';
import { FormsModule } from '@angular/forms';
import { ConfirmReport } from '../../Components/confirm-report/confirm-report';
import { environment } from '../../../environments/environment'
import { Report } from '../../services/report'
import { MatDialog } from '@angular/material/dialog';

//handling complex asynchronous operations
import {
  Observable, switchMap, forkJoin, map, catchError, EMPTY,
} from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop'
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { ToastService } from '../../services/ToastService';
import { confirmAction } from '../../Components/confirm-action/confirm-action';
import { DeletePostBtn } from '../../Components/delete-post-btn/delete-post-btn';
import { PostService } from '../../services/postService'; 


@Component({
  selector: 'app-profile',
  imports: [MatButtonModule,CommonModule, FormsModule, RouterLink, DatePipe, SlicePipe, confirmAction, ConfirmReport, DeletePostBtn],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  changeDetection: ChangeDetectionStrategy.OnPush,// Keep OnPush for performance!
})

export class Profile implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private tokenService: Token,
    private subService: SubscriptionService,
    private reportService: Report,
    private router: Router, private toast: ToastService,
    private destroyRef: DestroyRef,
    private postService: PostService
  ) { }

  baseUrl = environment.apiUrl
  reportUrl = `${this.baseUrl}/reports`
  // user!: User // or use any
  user = signal<any>(null);
  loading = signal(false);
  subscribers = signal(0)
  subscribed = signal(0)
  postsCount = signal(0)
  reason = signal('')
  postIdToDelete: number = -1
  msg = signal('')
  userPosts: any[] = []
  action_msg: string = ''

  isOwnProfile: boolean = false;
  isSubscribed = signal(false);
  reportProfile = false

  confirmErrMsg: String = ''

  reportRequest = {
    reportedUserId: 0,
    reason: ""
  }

  report = {
    reportProfile: false,
    reason: ''
  }

  dialogTitle = ''


  loadUser(username: String): Observable<any> {
    return this.http.get(`${this.baseUrl}/users/${username}`).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 403) {
          this.router.navigate(['/banned']);
        }
        else {
          this.msg = err.error.detail
        }
        return EMPTY
      })
    )
  }

  loadData() {

    this.loading.set(true)

    this.route.paramMap.pipe(
      //Get the username and load the user data
      map(params => params.get('username')),
      switchMap(username => {
        if (!username) {
          return EMPTY
        }
        return this.loadUser(username)
      }),

      //Once user is loaded, fetch all dependent data in parallel
      switchMap((user: User) => {
         console.log(user);
        
        this.user.set(user)
        this.isOwnProfile = user.username === this.tokenService.getUsername()
        this.reportRequest.reportedUserId = user.id

        //Use forkJoin to run all parallel API calls
        //forkJoin to wait for multiple HTTP calls simultaneously
        return forkJoin({
          subscribers: this.subService.countSubscribers(user.id),
          subscribed: this.subService.countSubscribed(user.id),
          postsCount: this.subService.countPosts(user.id),
          subscriptions: this.subService.getSubscriptions(),
          userPosts: this.postService.getProfilePosts(user.id)
        }).pipe(
          map(res => {
            //Set   count signals
            this.subscribers.set(res.subscribers)
            this.subscribed.set(res.subscribed)
            this.postsCount.set(res.postsCount)
            this.isSubscribed.set(res.subscriptions.some(u => u.username === user.username))
            this.userPosts = res.userPosts 
            
          })
        )
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: ( ) => {
        this.loading.set(false)
      },
      error: err => {
        this.loading.set(false)
      }
    })
  }
  ngOnInit(): void {

    this.loadData()
  }

  private timeout: any

  debuncedToggle() {
    if (this.timeout) {
      clearTimeout(this.timeout)
    }
    this.timeout = setTimeout(() => {
      this.toggleSubscription()
    }, 500);
  }


  toggleSubscription() {

    const req = this.isSubscribed() ? this.subService.unsubscribe(this.user().id)
      : this.subService.subscribe(this.user().id)

    ///subscribe to the observable
    req.subscribe({
      next: () => {
        this.msg.set("Done Successfully!")
        this.isSubscribed.update(current => !current)
        if (this.isSubscribed()) {

          this.toast.show('', "Subscribed Successfully")
        } else
          this.toast.show('', "Unsubscribed Successfully")
      },
      error: (err) => {
        this.toast.show('', err.error.detail)
      }
    })
  }

  hideConfirm() {
    this.report.reportProfile = false
    this.report.reason = ''
    this.confirmErrMsg = ''
  }

  //report a user 
  submitReport() {
    this.reportRequest.reason = this.report.reason;
    this.loading.set(true)

    this.reportService.submitReport(this.reportRequest).subscribe({
      next: () => {
        this.hideConfirm()
        this.loading.set(false)
        this.toast.show('', 'user reported successfully')

      },
      error: (err) => {
        this.loading.set(false)

        this.confirmErrMsg = err.error.detail
      },
      complete: () => {
        this.reportRequest.reason = ''
      }
    })
  }

  deletePost(id: number) {
    this.postIdToDelete = id
    this.action_msg = 'delete post?'
  }

  confirmDeletePostAction() {
    if (this.postIdToDelete > 0) {
      this.postService.deletePost(this.postIdToDelete).subscribe({
        next: () => {
          this.action_msg = ''
          this.postIdToDelete = -1

          this.toast.show('post deleted successfully', 'delete post')
          this.loadData()

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
  // edit post

  editPost(id: number) {
    this.router.navigate(['/edit-post', id])
  }

}
