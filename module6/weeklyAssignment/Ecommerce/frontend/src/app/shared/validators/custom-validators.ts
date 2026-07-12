import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  /**
   * Email validator
   */
  static email(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const email = control.value;
      if (!email) return null;

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(email) ? null : { invalidEmail: true };
    };
  }

  /**
   * Phone number validator
   */
  static phone(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const phone = control.value;
      if (!phone) return null;

      const phoneRegex = /^\d{10}$/;
      return phoneRegex.test(phone) ? null : { invalidPhone: true };
    };
  }

  /**
   * ZIP code validator
   */
  static zipCode(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const zip = control.value;
      if (!zip) return null;

      const zipRegex = /^\d{5}(-\d{4})?$/;
      return zipRegex.test(zip) ? null : { invalidZipCode: true };
    };
  }

  /**
   * Password strength validator
   */
  static passwordStrength(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.value;
      if (!password) return null;

      const hasUpperCase = /[A-Z]/.test(password);
      const hasLowerCase = /[a-z]/.test(password);
      const hasNumber = /[0-9]/.test(password);
      const hasSpecialChar = /[!@#$%^&*]/.test(password);
      const isLongEnough = password.length >= 8;

      const passwordValid = hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar && isLongEnough;
      return passwordValid ? null : { weakPassword: true };
    };
  }

  /**
   * Price validator
   */
  static price(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const price = control.value;
      if (!price) return null;

      const priceValue = parseFloat(price);
      return priceValue > 0 ? null : { invalidPrice: true };
    };
  }
}

