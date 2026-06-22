package org.example.springdatajpademo.Ecommerce.service;

import org.aspectj.weaver.ast.Or;
import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.OrderNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.model.OrderItem;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderItemRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderRepo;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/*to place order
* valid customer->valid product
* create order->create orderitem and link
* then save orderitem*/
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepo orderRepo;


    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Override
    public Order placeOrder(OrderRequestDTO request) {
         Product tproduct=productRepo.findById(request.getProductId()).orElseThrow(()-> new RuntimeException("Product not found"));

         Order saveorder;

         if(request.getOrderId()==null){
             Customer tcustomer=customerRepo.findById(request.getCustomerId()).orElseThrow(()->new CustomerNotFound("Customer not found"));
             Order torder=new Order();
             torder.setCustomer(tcustomer);

             saveorder=orderRepo.save(torder);
         }else{
             saveorder=orderRepo.findById(request.getOrderId()).orElseThrow(()->new OrderNotFound("Order not found"));
         }

         OrderItem torderitem=new OrderItem();
         torderitem.setOrder(saveorder);
         torderitem.setQuantity(request.getQuantity());
         torderitem.setProduct(tproduct);

         orderItemRepo.save(torderitem);

         return saveorder;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderRepo.findById(id).orElseThrow(()->new OrderNotFound("Order not found"));
    }


    @Override
    public void cancelOrder(Integer orderId) {
        orderRepo.deleteById(orderId);

    }

    @Override
    public List<Order> getOrdersByCustomer(Integer customerId) {
        return orderRepo.findOrderByCustomerId(customerId);
    }

    @Override
    public Order updateOrder(Integer id, Order order) {
        Order temporder=orderRepo.findById(id).orElseThrow(()->new OrderNotFound("Order is not found"));
        temporder.setCustomer(order.getCustomer());
        temporder.setOrderItems(order.getOrderItems());

        return orderRepo.save(temporder);
    }
}
