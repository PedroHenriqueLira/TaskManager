import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { FormsModule, NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { CommonModule } from '@angular/common';
import { InputMaskModule } from 'primeng/inputmask';
import { MessageModule } from 'primeng/message';
import { FloatLabelModule } from 'primeng/floatlabel';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputGroupModule } from 'primeng/inputgroup';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
  imports: [
    FormsModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    ToastModule,
    DialogModule,
    CommonModule,
    InputMaskModule,
    MessageModule,
    FloatLabelModule,
    InputGroupAddonModule,
    InputNumberModule,
    InputGroupModule,
  ],
  providers: [MessageService],
})
export class Login {
  email: string = '';
  password: string = '';
  visible: boolean = false;

  registerData = {
    nome: '',
    email: '',
    password: '',
    documento: '',
    endereco: '',
  };

  num1: number = 0;
  num2: number = 0;
  captchaAnswer: number | null = null;

  constructor(
    private router: Router,
    private messageService: MessageService,
    private authService: AuthService
  ) {
    this.generateCaptcha();
  }

  onSubmit() {
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Login realizado',
          detail: 'Você foi autenticado com sucesso!',
          life: 2000,
        });
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        const msg = err?.error?.message || 'Email ou senha inválidos';
        this.messageService.add({
          severity: 'error',
          summary: 'Erro de login',
          detail: msg,
          life: 2500,
        });
      },
    });
  }

  showRegisterModal(event: Event) {
    event.preventDefault();
    this.visible = true;
    this.resetRegisterForm();
    this.generateCaptcha();
  }

  cancelRegister(registerForm?: NgForm) {
    this.visible = false;
    if (registerForm) {
      registerForm.resetForm();
    }
    this.resetRegisterForm();
  }

  generateCaptcha() {
    this.num1 = Math.floor(Math.random() * 20) + 1;
    this.num2 = Math.floor(Math.random() * 20) + 1;
    this.captchaAnswer = null;
  }

  validateCaptcha(): boolean {
    return Number(this.captchaAnswer) === this.num1 + this.num2;
  }

  onRegister(registerForm: NgForm) {
    Object.values(registerForm.controls).forEach((control) => {
      control.markAsTouched();
    });

    if (registerForm.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro de Validação',
        detail: 'Por favor, preencha todos os campos corretamente.',
        life: 3000,
      });
      return;
    }

    if (!this.validateCaptcha()) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: 'Resposta da soma incorreta.',
        life: 1500,
      });
      return;
    }

    this.authService.register(this.registerData).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Usuário cadastrado com sucesso!',
          life: 2000,
        });

        this.visible = false;
        this.resetRegisterForm(registerForm);
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erro ao cadastrar usuário';
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: msg,
          life: 2500,
        });
      },
    });
  }

  resetRegisterForm(registerForm?: NgForm) {
    this.registerData = {
      nome: '',
      email: '',
      password: '',
      documento: '',
      endereco: '',
    };
    this.captchaAnswer = null;

    if (registerForm) {
      registerForm.resetForm();
    }
    this.generateCaptcha();
  }
}
