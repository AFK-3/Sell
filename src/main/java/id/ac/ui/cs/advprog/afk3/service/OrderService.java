package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order, String token);
    Order updateStatus(String orderId, String status, String token);
    Order findById(String orderId);
    List<Order> findAllByAuthor(String username);
    List<Order> findAllWithSeller(String username);
}
