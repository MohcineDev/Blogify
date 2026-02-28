//this service 
/*
centralize api logic
keep component clean and focus only on display and interaaction 
reuse same logic in multiple component (profile page -  home page - expolore users page)
*/

import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, throwError } from "rxjs";
import { User } from "../models/user";

@Injectable({ providedIn: 'root' }) // tells angular this class can be injected -used- in other places using dependency injection
//providedIn: 'root' means the srvice is available applcation-wide 
// it ensures a single instance of this service is shared across the whole aoo (singleton)
export class SubscriptionService {
    baseUrl = "http://localhost:8080/api/subscriptions"

    constructor(private http: HttpClient) {
    }

    //Observable represents a stream of data that you can subscribe to
    //typically from async sources like http requests

    getSubscriptions(): Observable<User[]> {
        return this.http.get<User[]>(this.baseUrl).pipe(
            catchError(err => this.handleError(err))
        )
    }

    subscribe(userId: number): Observable<any> {
        return this.http.post(`${this.baseUrl}/${userId}`, {}) 
    }

    unsubscribe(userId: number): Observable<any> {
        //This method allows you to chain (or pipe) operators onto the Observable to transform or handle the stream's data or errors before it's delivered to the subscriber
        return this.http.delete(`${this.baseUrl}/${userId}`).pipe(
            catchError(err => this.handleError(err))
        )
    }

    countSubscribers(id: number): Observable<any> {
        return this.http.get(`${this.baseUrl}/${id}/subscribers`).pipe(
            catchError(err => this.handleError(err))
        )
    }
    countSubscribed(id: number): Observable<any> {
        return this.http.get(`${this.baseUrl}/${id}/subscribed`).pipe(
            catchError(err => this.handleError(err))
        )
    }
    countPosts(id: number): Observable<any> {
        return this.http.get(`http://localhost:8080/api/posts/${id}/count`).pipe(
            catchError(err => this.handleError(err))
        )
    }


    handleError(error: any) {
        let msg = "an unexpected error occured."

        if (error.status === 400) {
            msg = error
        } else if (error.status === 404) {
            msg = "user not found"
        } else if (error.status === 409) {
            msg = "you are already subscribed"
        } else if (error.status === 500) {
            msg = "server error"
        }

        //emits an error to catch it in the component's subscribe()
        return throwError(() => new Error(msg))
    }
}