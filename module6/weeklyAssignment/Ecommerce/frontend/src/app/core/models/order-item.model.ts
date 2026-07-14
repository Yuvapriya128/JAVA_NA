import { OrderItemResponseDTO } from '../dto/order-item/orderitem.dto';

export interface OrderItemModel extends OrderItemResponseDTO {
  productName?: string;
}

