import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment'
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ConfirmReport } from '../../Components/confirm-report/confirm-report';
import { Report } from '../../services/report';
import { ToastService } from '../../services/ToastService';

@Component({
  selector: 'app-explore',
  imports: [RouterLink, CommonModule, ConfirmReport],
  templateUrl: './explore.html',
  styleUrl: './explore.css',
})
export class Explore implements OnInit {
  baseUrl = `${environment.apiUrl}/users/explore`
  users: any[] = []
  report = {
    reportProfile: false,
    reason: ''
  }

  loading = true
  isSubscribed = false
  isOwnProfile = false
  confirmErrMsg: String = ''

  hideLoadBtn: boolean = false
  hideLessBtn: boolean = false
  pageNumberUsers: number = 0
  pageSizeUsers: number = 12

  reportRequest = {
    reportedUserId: 0,
    reason: ""
  }

  constructor(private Http: HttpClient, private reportService: Report, private toast: ToastService,) { }

  ngOnInit() {
    this.getUsers()
  }

  hideConfirm() {
    this.report.reportProfile = false
    this.report.reason = ''
    this.confirmErrMsg = ''
  }

  submitReport() {
    this.reportRequest.reason = this.report.reason;
    this.loading = true

    this.reportService.submitReport(this.reportRequest).subscribe({
      next: () => {
        this.hideConfirm()
        this.loading = false
        this.toast.show('', 'user reported successfully')
      },
      error: (err) => {
        this.loading = false

        this.confirmErrMsg = err.error.detail
      },
      complete: () => {
        this.reportRequest.reason = ''
      }
    })
  }

  reportUser(id: number) {
    this.reportRequest.reportedUserId = id
    this.report.reportProfile = true
  }

  
  getUsers() {
    this.loading = true
    this.Http.get<any[]>(`${this.baseUrl}?page=${this.pageNumberUsers}&size=${this.pageSizeUsers}`).subscribe({
      next: (data) => {
        this.users = data
        data.length < this.pageSizeUsers ? this.hideLoadBtn = true : this.hideLoadBtn = false
        this.pageNumberUsers <= 0 ? this.hideLessBtn = true : this.hideLessBtn = false
        window.scroll({
          behavior: 'smooth',
          top: 0
        })
        this.loading = false
      },
      error: (err) => {
        this.loading = false
      },
    })
  }

  loadmore() {
    this.pageNumberUsers++;
    this.getUsers()

  }

  loadless() {
    this.pageNumberUsers--;
    this.getUsers()
  }

}
