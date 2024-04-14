package id.ac.ui.cs.advprog.afk3.repository;

import java.util.ArrayList;
import java.util.List;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    private List<Order> orderData = new ArrayList();
    public Order save(Order order){
        int i =0;
        for (Order savedOrder :orderData){
            if (savedOrder.getId().equals(order.getId())){
                orderData.remove(i);
                orderData.add(i, order);
                return order;
            }
            i+=1;
        }
        orderData.add(order);
        return order;
    }
    public Order findById(String id){
        for (Order savedOrder : orderData){
            if (savedOrder.getId().toString().equals(id)){
                return savedOrder;
            }
        }
        return null;
    }
    public List<Order> findAllByAuthor(String authorUsername){
        List<Order> result = new ArrayList<>();
        for (Order savedOrder : orderData){
            if (savedOrder.getAuthorUsername().equals(authorUsername)){
                result.add(savedOrder);
            }
        }
        return result;
    }

    public List<Order> findAllWithSeller(String sellerUsername){
        List<Order> result = new ArrayList<>();
        for (Order savedOrder : orderData){
            for (Listing listing : savedOrder.getListings()){
                if(sellerUsername.equals(listing.getSellerUsername())){
                    result.add(savedOrder);
                }
            }
        }
        return result;
    }
}
