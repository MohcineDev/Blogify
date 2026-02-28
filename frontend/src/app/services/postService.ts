import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { catchError, Observable, throwError } from 'rxjs'
import { environment } from '../../environments/environment' 
@Injectable({ providedIn: "root" })

export class PostService {
    baseUrl = `${environment.apiUrl}/posts`

    constructor(private http: HttpClient) { }

    createPost(formData: FormData): Observable<any> {
        return this.http.post(this.baseUrl, formData)
    }


    uploadMedia(formData: FormData): Observable<{ url: string }> {
        return this.http.post<{ url: string }>(`${this.baseUrl}/media/upload`, formData)
    }


    getPost(id: number): Observable<any> {
        return this.http.get(`${environment.apiUrl}/posts/${id}`).pipe(
            catchError(err => this.handleError(err))
        )
    }
    deletePost(id: number) {
        return this.http.delete(`${this.baseUrl}/${id}`)
    }

  getProfilePosts(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/profile/${id}`)
  }


    private handleError(error: any) {

        let msg = "an unexpected error occured."

        if (error.status === 400) {
            msg = "invalid req"
        } else if (error.status === 404) {
            msg = "post not found"
        } else if (error.status === 409) {
            msg = "you are already subscribed"
        } else if (error.status === 500) {
            msg = "server error"
        }

        //emits an error to catch it in the component's subscribe()
        return throwError(() => error)
    }

}