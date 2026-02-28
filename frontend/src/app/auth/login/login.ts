import { Component } from '@angular/core';
import { Auth } from '../../services/auth';
import { Token } from '../../services/token';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router'; 
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/ToastService';
@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: '../auth.css'
})

export class Login {
  loading = false
  loggedUser: string | null = null
  submitted = false;
  myForm!: FormGroup
  dialogTitle = ''

  constructor(private authService: Auth, public token: Token, private toast: ToastService, private router: Router,  private fb: FormBuilder) {
    this.myForm = this.fb.group({
      identifier: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
    })
  }



  onSubmit() {
    if (this.myForm.invalid) {
      return
    }

    this.loading = true;

    const loginData = {
      identifier: this.myForm.get('identifier')?.value,
      password: this.myForm.get('password')?.value
    }


    this.authService.login(loginData).subscribe({
      next: res => {
        localStorage.setItem('token', res.token)
        const decoded = this.token.decodeToken()
        if (decoded?.role == 'ADMIN') {
          this.router.navigate(['/dashboard'])
        } else {
          this.router.navigate(['/home'])
        }

        this.loggedUser = decoded?.sub ?? null
        this.loading = false;
      },
      error: err => {
        this.toast.show('', err.error.detail)

        this.loading = false;
      }
    })
  }
}
