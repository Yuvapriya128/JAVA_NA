// Customer DTOs
export interface CustomerRequestDTO {
  name: string;
  email: string;
  address: string;
  password: string;
  phoneNumber?: string;
}

export interface CustomerResponseDTO {
  id: number;
  name: string;
  email: string;
  address: string;
  role?: string;
  phoneNumber?: string;
  joinedDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CustomerUpdateDTO {
  name?: string;
  email?: string;
  address?: string;
  phoneNumber?: string;
}

export interface AdminCustomerRequestDTO extends CustomerRequestDTO {
  username: string;
  password: string;
}

export interface ChangeRoleRequestDTO {
  customerId: number;
  newRole: string;
}

export interface CustomerMeResponseDTO {
  id?: number;
  customerId?: number;
  firstName?: string;
  lastName?: string;
  name?: string;
  customerName?: string;
  fullName?: string;
  email?: string;
  customerEmail?: string;
  address?: string;
  customerAddress?: string;
  role?: string;
  userRole?: string;
  phone?: string;
  phoneNumber?: string;
  phoneNo?: string;
  mobile?: string;
  mobileNumber?: string;
}

export interface CustomerMeUpdateDTO {
  name?: string;
  firstName?: string;
  lastName?: string;
  address: string;
  phoneNumber?: string;
  phone?: string;
  email?: string;
}

export interface CustomerPasswordChangeDTO {
  currentPassword: string;
  newPassword: string;
  confirmPassword?: string;
}
