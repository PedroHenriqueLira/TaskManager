import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-not-authorized',
  imports: [
    CommonModule,
    ButtonModule,
    RouterLink],
  templateUrl: './not-authorized.html',
  styleUrl: './not-authorized.scss'
})
export class NotAuthorized {
  

}
