// Order DTOs
export interface OrderRequestDTO {
  customerId: number;
  productId: number;
  quantity: number;
  orderDate: string;
  totalAmount: number;
  status: string;
  paymentMethod?: string;
  paymentStatus?: string;
}

export interface OrderResponseDTO {
  id: number;
  customerId: number;
  orderDate: string;
  totalAmount: number;
  status: string;
  paymentMethod?: string;
  paymentStatus?: string;
  createdAt: string;
  updatedAt: string;
}

export interface OrderUpdateDTO {
  id?: number;
  status?: string;
  totalAmount?: number;
}

