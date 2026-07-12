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

