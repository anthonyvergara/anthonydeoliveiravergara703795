import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthFacade } from '../../../../core/auth/facade/auth.facade';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  errorMessage = '';
  returnUrl = '/home';

  constructor(
    private fb: FormBuilder,
    private authFacade: AuthFacade,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home';
  }

  private initForm(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authFacade.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Usuário ou senha inválidos. Tente novamente.';
        console.error('Login error:', error);
      }
    });
  }
}

