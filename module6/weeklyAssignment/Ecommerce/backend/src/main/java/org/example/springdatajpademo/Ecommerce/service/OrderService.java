package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO placeOrder(OrderRequestDTO request);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Integer id);

    void cancelOrder(Integer orderId);

    List<OrderResponseDTO> getOrdersByCustomer(Integer customerId);

    List<OrderResponseDTO> getMyOrders();

    OrderResponseDTO updateOrder(Integer id,
                                 OrderUpdateDTO dto);
}