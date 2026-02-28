import { Component } from '@angular/core';
import { Auth } from '../../services/auth';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router'; 
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/ToastService';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: '../auth.css'
})

export class Register {
  message = ""
  loading = false
  myForm!: FormGroup
  dialogTitle = ''

  confirmPassValidator(control: AbstractControl): ValidationErrors | null {
    const pass = control.get('password')
    const confirmPass = control.get('confirmPassword')

    return pass && confirmPass && pass.value == confirmPass.value ?
      null
      : { passwordMismatch: true }
  }


  constructor(private authService: Auth, private router: Router, private toast: ToastService, private dialog: MatDialog, private fb: FormBuilder) {
    this.myForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), Validators.pattern('^[a-zA-Z0-9]+$')]],  
      email: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(30), Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$')]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]]
    }, { validators: this.confirmPassValidator })
  }


 

  register() {
    if (this.myForm.invalid) {
      return
    }
    this.loading = true;

    const { username, email, password } = this.myForm.value
    const registrationData = { username, email, password }

    this.authService.register(registrationData).subscribe({
      next: res => {
        this.message = "registered " + res
        this.loading = false
        this.router.navigate(['/login'])

      },
      error: err => {

        let er = JSON.parse(err.error)
 
        this.toast.show('', er.detail)
        this.loading = false
      }

    })
  }

}
