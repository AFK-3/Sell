package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import id.ac.ui.cs.advprog.afk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderBuilder orderBuilder;

    @Autowired
    private UserRepository userRepository;

    public Order createOrder(Order order){
        User author = userRepository.findByUsername(order.getAuthorUsername());
        if (order.getId()==null || orderRepository.findById(order.getId().toString())==null &&
                (author.getType().equals(UserType.BUYERSELLER.name())
                || author.getType().equals(UserType.BUYER.name())))
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
