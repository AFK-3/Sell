package id.ac.ui.cs.advprog.afk3.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
    Optional<List<Order>> findOrdersByListings_Id(String id);
    List<Order> findAllByAuthorUsername(String authorUsername);
    Optional<List<Order>> findAllByListings_SellerUsername(String sellerUsername);
}
