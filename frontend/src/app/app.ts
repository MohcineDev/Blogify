import { Component, OnInit, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { Nav } from './Components/nav/nav'


@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    FormsModule,
    Nav],
  templateUrl: './app.html'
})
export class App implements OnInit {
  protected readonly title = signal('frontend');
  showNav = false
  backendMessage = '';

  constructor(private http: HttpClient, private router: Router) { }

  checkBackend() {
    this.http.get('http://localhost:8080/api/posts', { responseType: 'text' })
      .subscribe({
        next: res => this.backendMessage = '✅ Backend responded: ' + res,
        error: err => this.backendMessage = '❌ Backend error: ' + err.message
      });
  }

  ngOnInit() {
    this.router.events.subscribe((e) => {

      //scroll to top on route change
      if (e instanceof NavigationEnd ) {
        scrollTo(0, 0)
      }

      //display navbar
      const url = this.router.url
      this.showNav = !(url.includes('login') || url.includes('register'))
    })
  }

}

