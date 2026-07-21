export interface CreateCustomerRequest {
  customerName: string;
  email: string;
  password: string;
  phoneNumber: string;
  city: string;
  creditScore: number;
  role: string;
}

export interface CustomerResponse {
  customerId: number;
  customerName: string;
  email: string;
  phoneNumber: string;
  city: string;
  creditScore: number;
  role: string;
  active: boolean;
}

export interface UpdateCustomerRequest {
  customerName: string;
  email: string;
  phoneNumber: string;
  city: string;
  creditScore: number;
  role: string;
  active: boolean;
}
