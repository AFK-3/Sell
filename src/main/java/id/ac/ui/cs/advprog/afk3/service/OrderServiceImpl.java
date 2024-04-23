package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import id.ac.ui.cs.advprog.afk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderBuilder orderBuilder;

    @Autowired
    private UserRepository userRepository;

    public Order createOrder(Order order, String token){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> authorData = restTemplate.exchange("http://localhost:8080/user/get-role", HttpMethod.GET,entity ,String.class);

        if (order.getId()==null || orderRepository.findById(order.getId().toString())==null &&
                (UserType.BUYERSELLER.name().equals(authorData.getBody())
                || UserType.BUYER.name().equals(authorData.getBody())))
        {
            Order newOrder = orderBuilder.reset().setCurrent(order).firstSetUp().build();
            orderRepository.save(newOrder);
            return newOrder;
        }
        return null;
    }
    @Override
    public Order updateStatus(String orderId, String status){
        Order order = findById(orderId);
        if (order != null) {
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
}
