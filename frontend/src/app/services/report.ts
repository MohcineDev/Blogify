import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { SubscriptionService } from './subscription'
@Injectable({
  providedIn: 'root',
})
export class Report {
  baseUrl = environment.apiUrl

  constructor(private http: HttpClient,
    private subSer: SubscriptionService
  ) { }

  submitReport(payload: any) {
    return this.http.post(`${this.baseUrl}/reports`, payload).pipe(
      catchError(err => throwError(() => err))
    )
  }


}
