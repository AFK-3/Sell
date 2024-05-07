package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
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

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ListingRepository listingRepository;

    private final OrderBuilder orderBuilder = new OrderBuilder();

    @Value("${app.auth-domain}")
    String authUrl;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Order createOrder(Order order, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> authorData = restTemplate.exchange(authUrl+"/user/get-role", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"/user/get-username", HttpMethod.GET,entity ,String.class);

        System.out.println(order.getId()+" "+authorData.getBody()+" "+owner.getBody());
        if (isUserBuyer(authorData.getBody())
                && isAuthorAccessing(owner, order))
        {
            // Kurangin semua quantitas listing
            // Kalo udah habis / melebihi stok, otomatis batal
            List<Listing> outOfStock = new ArrayList<>();
            for (Listing listing : order.getListings()){
                Listing exist = listingRepository.findById(listing.getId().toString());
                if (exist.getQuantity()-listing.getQuantity()>=0) {
                    exist.setQuantity(exist.getQuantity() - listing.getQuantity());
                }else{
                    outOfStock.add(exist);
                }
            }
            for (Listing l: outOfStock){
                for (int i=0; i<order.getListings().size(); i++){
                    if (order.getListings().get(i).getId().equals(l.getId())){
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

    @Override
    public void deleteAllWithListing(Listing listing) {
        orderRepository.deleteAllWithListing(listing);
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
