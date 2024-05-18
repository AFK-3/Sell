package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.Security.JwtValidator;
import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ListingRepository listingRepository;

    private final OrderBuilder orderBuilder = new OrderBuilder();

    @Value("${app.auth-domain}")
    String authUrl;

    @Autowired
    JwtValidator validator;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Order createOrder(Order order, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> authorData = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);
        String owner = validator.getUsernameFromJWT(token);

        System.out.println(order.getId()+" "+authorData.getBody()+" "+owner);
        if (isUserBuyer(authorData.getBody())
                && isAuthorAccessing(owner, order))
        {
            // Kurangin semua quantitas listing
            // Kalo udah habis / melebihi stok, otomatis batal
            List<Listing> outOfStock = new ArrayList<>();
            for (Listing listing : order.getListings()){
                Optional<Listing> exist = listingRepository.findById(listing.getId());
                if (exist.isPresent() && exist.get().getQuantity()-listing.getQuantity()>=0) {
                    exist.get().setQuantity(exist.get().getQuantity() - listing.getQuantity());
                }else exist.ifPresent(outOfStock::add);
            }
            for (Listing l: outOfStock){
                for (int i=0; i<order.getListings().size(); i++){
                    if (order.getListings().get(i).getId().equals(l.getId())){
                        log.error("listing {} out of stock",order.getListings().get(i).getId());
                        order.getListings().remove(i);
                        break;
                    }
                }
            }
            Order newOrder = orderBuilder.reset().setCurrent(order)
                    .addListings(order.getListings())
                    .firstSetUp()
                    .build();
            System.out.println(newOrder);
            orderRepository.save(newOrder);
            log.info("order {} placed successful",newOrder.getId());
            return newOrder;
        }
        return null;
    }
    @Override
    public Order updateStatus(String orderId, String status, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        String sellerUsername = validator.getUsernameFromJWT(token);

        Order order = findById(orderId);
        if (order==null){
            throw new NoSuchElementException();
        }
        boolean sellerFound = false;
        for (Listing l : order.getListings()){
            if (l.getSellerUsername().equals(sellerUsername)){
                sellerFound = true;
                break;
            }
        }
        if (sellerFound) {
            Order newOrder = orderBuilder.setCurrent(order).addStatus(status).build();
            log.info("order {} placed successful",newOrder.getId());
            orderRepository.save(newOrder);
            return newOrder;
        }else{
            log.error("order FAILED to be updated");
            return null;
        }

    }

    @Override
    public Order findById(String orderId){
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(value -> log.info("order {} found", value.getId()));
        return order.orElse(null);
    }

    @Override
    public List<Order> findAllByAuthor(String authorUsername){
        List<Order> order = orderRepository.findAllByAuthorUsername(authorUsername);
        log.info("orders found by {}", authorUsername);
        return order;
    }

    @Override
    public List<Order> findAllWithSeller(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        String loggedin = validator.getUsernameFromJWT(token);
        Optional<List<Order>> result = orderRepository.findAllByListings_SellerUsername(loggedin);
        return result.orElse(null);
    }

    @Override
    public void deleteAllWithListing(Listing listing) {
        log.info("orders deleted by with listing {}", listing.getId());
        orderRepository.deleteOrdersByListings_Id(listing.getId());
    }

    private boolean isUserBuyer(String role){
        if (UserType.BUYERSELLER.name().equals(role)) return true;
        else return UserType.BUYER.name().equals(role);
    }

    private  boolean isAuthorAccessing(String author, Order order){
        if (author!=null) return order.getAuthorUsername().equals(author);
        return false;
    }
}
