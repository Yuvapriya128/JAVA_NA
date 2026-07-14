// OrderItem DTOs
export interface OrderItemRequestDTO {
  orderId: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface OrderItemResponseDTO {
  id: number;
  orderId: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItemUpdateDTO {
  id?: number;
  quantity?: number;
  unitPrice?: number;
  totalPrice?: number;
}

