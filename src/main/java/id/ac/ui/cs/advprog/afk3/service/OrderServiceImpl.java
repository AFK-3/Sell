package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    private final OrderBuilder orderBuilder = new OrderBuilder();

    @Value("${app.auth-domain}")
    String authUrl;

    RestTemplate restTemplate = new RestTemplate();

    public Order createOrder(Order order, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> authorData = restTemplate.exchange(authUrl+"/user/get-role", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"/user/get-username", HttpMethod.GET,entity ,String.class);

        if (isOrderValid(order)
                && isUserBuyer(authorData.getBody())
                && isAuthorAccessing(owner, order))
        {
            Order newOrder = orderBuilder.reset().setCurrent(order).firstSetUp().build();
            orderRepository.save(newOrder);
            return newOrder;
        }
        return null;
    }
    @Override
    public Order updateStatus(String orderId, String status, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> loggedin = restTemplate.exchange(authUrl+"/user/get-username", HttpMethod.GET,entity ,String.class);

        String sellerUsername = loggedin.getBody();

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
            orderRepository.save(newOrder);
            return newOrder;
        }else{
            throw new NoSuchElementException();
        }
    }

    @Override
    public Order findById(String orderId){
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findAllByAuthor(String authorUsername){
        return orderRepository.findAllByAuthor(authorUsername);
    }

    @Override
    public List<Order> findAllWithSeller(String username) {
        return orderRepository.findAllWithSeller(username);
    }

    private boolean isOrderValid(Order order){
        return orderRepository.findById(order.getId().toString()) == null;
    }

    private boolean isUserBuyer(String role){
        if (UserType.BUYERSELLER.name().equals(role)) return true;
        else return UserType.BUYER.name().equals(role);
    }

    private  boolean isAuthorAccessing(ResponseEntity<String> author, Order order){
        if (author.getBody()!=null) return order.getAuthorUsername().equals(author.getBody());
        return false;
    }
}
