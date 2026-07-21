import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  private readonly fb = inject(FormBuilder);

  readonly categories = ['General Enquiry', 'Product Demo', 'Partnership', 'Support', 'Careers'];
  readonly contactMethods = ['Email', 'Phone', 'WhatsApp'];
  readonly submitted = signal(false);

  readonly contactForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50), Validators.pattern(/^[A-Za-z ]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    subject: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    category: ['', [Validators.required]],
    message: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(500)]],
    preferredContactMethod: ['', [Validators.required]],
    agreedToPrivacy: [false, [Validators.requiredTrue]]
  });

  control(name: keyof typeof this.contactForm.controls) {
    return this.contactForm.controls[name];
  }

  submit(): void {
    if (this.contactForm.invalid) {
      this.contactForm.markAllAsTouched();
      return;
    }

    this.submitted.set(true);
    this.contactForm.reset({
      name: '',
      email: '',
      phone: '',
      subject: '',
      category: '',
      message: '',
      preferredContactMethod: '',
      agreedToPrivacy: false
    });
  }

  reset(): void {
    this.submitted.set(false);
    this.contactForm.reset({
      name: '',
      email: '',
      phone: '',
      subject: '',
      category: '',
      message: '',
      preferredContactMethod: '',
      agreedToPrivacy: false
    });
  }
}
