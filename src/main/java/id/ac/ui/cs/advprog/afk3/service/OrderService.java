package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Map<String, Integer> order, String token);
    Order updateStatus(String orderId, String status, String token);
    Order findById(String orderId);
    List<Order> findAllByAuthor(String username);
    List<Order> findAllWithSeller(String username);
    void failAllWithListing(Listing listing);
}
