package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.Security.JwtValidator;
import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.hibernate.Filter;
import org.hibernate.Session;
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

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private EntityManager entityManager;

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
    public Order createOrder(Map<String, Integer> order, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> authorData = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);
        String owner = validator.getUsernameFromJWT(token);

        if (isUserBuyer(authorData.getBody()))
        {
            // Kurangin semua quantitas listing
            // Kalo udah habis / melebihi stok, otomatis batal
            List<Listing> outOfStock = new ArrayList<>();
            List<Listing> purchased = new ArrayList<>();
            Map<String, Integer> quantity = new HashMap<>();

            for (String listingId : order.keySet()){

                Optional<Listing> exist = listingRepository.findById(listingId);

                if (exist.isPresent() && !exist.get().isDeleted() && exist.get().getQuantity()-order.get(listingId)>=0) {
                    exist.get().setQuantity(exist.get().getQuantity() - order.get(listingId));
                    purchased.add(exist.get());
                    quantity.put(exist.get().getId(), order.get(listingId));
                }else exist.ifPresent(outOfStock::add);
            }
            for (Listing l: outOfStock){
                for (String listingId : order.keySet()){
                    if (listingId.equals(l.getId())){
                        log.error("listing {} out of stock",listingId);
                        order.remove(listingId);
                        break;
                    }
                }
            }
//            order.setListingQuantity(purchased);
            Order newOrder = orderBuilder.reset()
                    .addAuthor(owner)
                    .addListings(purchased)
                    .firstSetUp()
                    .build();
            newOrder.setListingQuantity(quantity);
            System.out.println(newOrder);
            newOrder = orderRepository.save(newOrder);
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
        if (order.getStatus().equals(OrderStatus.SUCCESS.name())){
            log.info("order {} has already been solved",order.getId());
            return null;
        }
        if (order.getStatus().equals(OrderStatus.CANCELLED.name())){
            log.info("order {} has already been cancelled",order.getId());
            return null;
        }
        if (order.getStatus().equals(OrderStatus.FAILED.name())){
            log.info("order {} has missing item",order.getId());
            return null;
        }
        if (sellerFound) {
            Order newOrder = orderBuilder.setCurrent(order).addStatus(status).build();
            log.info("order {} status change successful",newOrder.getId());
            orderRepository.save(newOrder);
            return newOrder;
        }else{
            log.info("order NOT to be updated");
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

        List<Order> resultWaitingPayment = getListWithSellerUsernameStatus(loggedin, OrderStatus.WAITINGPAYMENT.name());
        List<Order> resultFailed = getListWithSellerUsernameStatus(loggedin, OrderStatus.FAILED.name());
        List<Order> resultSuccess = getListWithSellerUsernameStatus(loggedin, OrderStatus.SUCCESS.name());
        List<Order> resultCancelled = getListWithSellerUsernameStatus(loggedin, OrderStatus.CANCELLED.name());

        List<Order> result = new ArrayList<>();
        Stream.of(resultCancelled, resultFailed, resultSuccess, resultWaitingPayment).forEach(result::addAll);
        return result;
    }

    @Override
    public void failAllWithListing(Listing listing) {
        log.info("orders deleted by with listing {}", listing.getId());
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        Optional<List<Order>>result = orderRepository.findOrdersByListings_Id(listing.getId());
        session.disableFilter("deletedProductFilter");

        if(result.isPresent()){
            for(Order order : result.get()){
                Order newOrder= orderBuilder.setCurrent(order).addStatus(OrderStatus.FAILED.name()).build();
                orderRepository.save(newOrder);
            }
        }
    }

    private List<Order> getListWithSellerUsernameStatus(String sellerUsername, String orderStatus){
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", true);
        List<Order> result = orderRepository.findAllByListings_SellerUsername_AndStatus(sellerUsername, orderStatus);
        session.disableFilter("deletedProductFilter");
        return result;
    }

    private boolean isUserBuyer(String role){
        if (UserType.BUYERSELLER.name().equals(role)) return true;
        else return UserType.BUYER.name().equals(role);
    }

}
