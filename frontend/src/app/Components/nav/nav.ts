import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Token } from '../../services/token';
import { Router, RouterLink } from '@angular/router';
import { confirmAction } from '../../Components/confirm-action/confirm-action'
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../../services/ToastService';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, confirmAction],
  templateUrl: './nav.html',
  styleUrl: './nav.css',
})

export class Nav implements OnInit {
  private localTheme = localStorage.getItem('theme');
  username: any
  role: string = "user"
  action_msg: string = ''
  apiUrl = environment.apiUrl
  notifications: any[] = []
  loading = false
  hide_nav = true

  constructor(private http: HttpClient, public tokenService: Token, private toast: ToastService,
    private router: Router) {
  }

  dialogConfig = {
    title: '',
    content: ''
  }


  ngOnInit() {
    this.username = this.tokenService.getUsername()
    this.role = this.tokenService.getRole() || "user"
    this.getNotifs()
    this.getUnreadCount()
    console.log( localStorage.getItem('theme'));
    
    this.applyTheme() 
  }

  confirmLogoutAction() {
    this.tokenService.logout()
    this.action_msg = ''
    this.router.navigate(['/login'])
  }

  cancelLogoutAction() {
    this.action_msg = ''
  }

  logout() {
    this.action_msg = 'logout!!'
  } 

  toggleDark() {
    this.localTheme = this.localTheme === "dark" ? 'light' : 'dark'
    localStorage.setItem("theme", this.localTheme);
    this.applyTheme()
  }

  applyTheme() {
    this.localTheme = localStorage.getItem('theme');
    if (this.localTheme == "dark") {
      document.body.classList.add("dark");
    } else {
      document.body.classList.remove("dark");
    }

  }
 
  //------- Notifications

  displayNotifications = false
  unreadCount = 0

  displayNotifs() {
    this.displayNotifications = !this.displayNotifications
  }

  getNotifs() {
    this.loading = true

    this.http.get<any[]>(`${this.apiUrl}/notif`).subscribe({
      next: (res) => {
        this.loading = false
        this.notifications = res; 
      },
      error: (err) => {
        this.loading = false
      }
    })
  }

  toggleRead(n: any) {
    this.loading = true
    this.http.put(`${this.apiUrl}/notif/${n.id}/toggle`, {}).subscribe({
      next: () => {
        this.loading = false
        n.isRead = !n.isRead
        this.unreadCount += n.isRead ? -1 : 1

      },
      error: (err) => {
        this.loading = false
        // update the list
        this.getNotifs()
        this.toast.show(err.error.detail || 'failed to toggle notif')
      }
    })
  }
  markRead(n: any) {
    this.loading = true
    this.http.put(`${this.apiUrl}/notif/${n.id}/read`, {}).subscribe({
      next: () => {
        this.loading = false
        if (!n.isRead) {
          n.isRead = true
          this.unreadCount -= 1
        }
      },
      error: (err) => {
        this.loading = false
        // update the list
        this.getNotifs()
      }
    })
  }
  getUnreadCount() {
    this.http.get<number>(`${this.apiUrl}/notif/unread`).subscribe(res => this.unreadCount = res)
  }

  //delete notification
  deleteNotif(id: number) {
    this.http.delete(`${this.apiUrl}/notif/${id}`).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(notif => notif.id != id)
        this.unreadCount--
        this.toast.show('notif deleted successfully')
      },
      error: (err) => {
        this.toast.show(err.error.detail || 'failed to delete notif') 
        // update the list
        this.getNotifs()
      }
    })

  }
}
