import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private apiUrl = 'http://localhost:8080/api/auth'

  constructor(private http: HttpClient) {

  }

  register(data: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, data, {
      responseType: 'text'
    })
  }

  login(credentials: any): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, credentials)
  }
}
