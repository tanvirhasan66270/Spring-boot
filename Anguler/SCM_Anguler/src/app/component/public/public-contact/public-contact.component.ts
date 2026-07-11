import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-public-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './public-contact.component.html',
  styleUrls: ['./public-contact.component.css']
})
export class PublicContactComponent {
  feedback = { name: '', email: '', subject: '', message: '' };

  submitForm(): void {
    if (!this.feedback.name || !this.feedback.email || !this.feedback.message) {
      alert('Please fill out all required fields.');
      return;
    }
    alert(`Thank you for reaching out, ${this.feedback.name}! Our customer service team will get back to you within 24 hours.`);
    this.feedback = { name: '', email: '', subject: '', message: '' };
  }
}
