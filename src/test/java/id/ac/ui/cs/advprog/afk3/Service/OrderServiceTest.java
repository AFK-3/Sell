package id.ac.ui.cs.advprog.afk3.Service;

import id.ac.ui.cs.advprog.afk3.Security.JwtValidator;
import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import id.ac.ui.cs.advprog.afk3.service.OrderServiceImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceTest {


    @Mock
    private EntityManager entityManager;
    @InjectMocks
    OrderServiceImpl orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ListingRepository listingRepository;

    @Mock
    JwtValidator validator;
    List<Order> orders;

    private OrderBuilder builder = new OrderBuilder();
    private ListingBuilder listingBuilder = new ListingBuilder();

    private String token = "a";

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        List<Listing> listings = new ArrayList<>();
        Listing listing1 = listingBuilder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addSellerUsername("penjual1")
                .addQuantity(2)
                .addName("Sampo Cap Bambang")
                .build();

        listings.add(listing1);

        orders = new ArrayList<>();
        Order order1 = builder.reset().firstSetUp()
                .addAuthor("Safira Sudarajat")
                .addListings(listings)
                .build();
        orders.add(order1);

        List<Listing> listings2 = new ArrayList<>();
        Listing listing2 = listingBuilder.reset()
                .addId(UUID.fromString("12345678-1c39-460e-8860-71af6af63bd6"))
                .addSellerUsername("penjual1")
                .addQuantity(10)
                .addName("Sampo Cap penjual1")
                .build();
        listings2.add(listing1);
        listings2.add(listing2);

        Order order2 = builder.reset().firstSetUp()
                .addAuthor("Safira Sudarajat")
                .addListings(listings2)
                .build();
        orders.add(order2);
    }

    @AfterEach
    void dropAll(){
        orders=null;
    }

    @Test
    void testCreateOrderBuyer(){
        Order order =  orders.getFirst();
        ResponseEntity<String> re = new ResponseEntity<String>("Safira Sudarajat", HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>("BUYER", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Map<String, Integer> orderPurchase = new HashMap<>();
        for(Listing l : order.getListings()){
            orderPurchase.put(l.getId(), l.getQuantity());
        }

        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role", HttpMethod.GET, entity,String.class))
                .thenReturn(re2);
        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(listingRepository.findById(order.getListings().getFirst().getId())).thenReturn(Optional.of(order.getListings().getFirst()));
        Order result  = orderService.createOrder(orderPurchase, token);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void testCreateOrderBuyerSeller(){
        Order order =  orders.getFirst();
        ResponseEntity<String> re = new ResponseEntity<String>("Safira Sudarajat", HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>("BUYERSELLER", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role", HttpMethod.GET, entity,String.class))
                .thenReturn(re2);

        Map<String, Integer> orderPurchase = new HashMap<>();
        for(Listing l : order.getListings()){
            orderPurchase.put(l.getId(), l.getQuantity());
        }

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(listingRepository.findById(order.getListings().getFirst().getId())).thenReturn(Optional.of(order.getListings().getFirst()));
        Order result  = orderService.createOrder(orderPurchase, token);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void testCreateOrderIfInvalidJWT(){
        Order order =  orders.get(1);
        ResponseEntity re = new ResponseEntity(null, HttpStatus.OK);
        ResponseEntity re2 = new ResponseEntity<>(null, HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role", HttpMethod.GET, entity,String.class))
                .thenReturn(re2);

        Map<String, Integer> orderPurchase = new HashMap<>();
        for(Listing l : order.getListings()){
            orderPurchase.put(l.getId(), l.getQuantity());
        }

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(null);
        assertNull(orderService.createOrder(orderPurchase, token));
        verify(orderRepository, times(0)).save(order);
    }

    @Test
    void testCreateOrderIfInvalidRole(){
        Order order =  orders.get(1);
        ResponseEntity<String> re = new ResponseEntity<String>("Safira Sudarajat", HttpStatus.OK);
        ResponseEntity re2 = new ResponseEntity<>(null, HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role", HttpMethod.GET, entity,String.class))
                .thenReturn(re2);

        Map<String, Integer> orderPurchase = new HashMap<>();
        for(Listing l : order.getListings()){
            orderPurchase.put(l.getId(), l.getQuantity());
        }

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        assertNull(orderService.createOrder(orderPurchase, token));
        verify(orderRepository, times(0)).save(order);
    }

    @Test
    void testCreateOrderInvalidQuantity(){
        Order order =  orders.get(0);
        ResponseEntity<String> re = new ResponseEntity<String>("Safira Sudarajat", HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>("BUYER", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Listing listingExist = listingBuilder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addQuantity(1)
                .build();

        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role", HttpMethod.GET, entity,String.class))
                .thenReturn(re2);

        Map<String, Integer> orderPurchase = new HashMap<>();
        for(Listing l : order.getListings()){
            orderPurchase.put(l.getId(), l.getQuantity());
        }

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        when(listingRepository.findById(order.getListings().getFirst().getId().toString())).thenReturn(Optional.of(listingExist));
        assertThrows(IllegalArgumentException.class,()->{orderService.createOrder(orderPurchase, token);});
    }

    @Test
    void testCreateOrderEmptyList(){
        Order order =  orders.get(0);
        order.setListings(new ArrayList<>());
        ResponseEntity<String> re = new ResponseEntity<String>("Safira Sudarajat", HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>("BUYER", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role", HttpMethod.GET, entity,String.class))
                .thenReturn(re2);

        Map<String, Integer> orderPurchase = new HashMap<>();
        for(Listing l : order.getListings()){
            orderPurchase.put(l.getId(), l.getQuantity());
        }

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        assertThrows(IllegalArgumentException.class,()->{orderService.createOrder(orderPurchase, token);});
    }

    @Test
    void testUpdateStatus(){
        Order order = orders.get(1);
        Order newOrder = new Order();
        order.setId(order.getId());
        newOrder = builder.setCurrent(newOrder)
                .addStatus(OrderStatus.WAITINGPAYMENT.name())
                .build();

        ResponseEntity<String> re = new ResponseEntity<String>("penjual1", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());
        doReturn(newOrder).when(orderRepository).save(any(Order.class));
        Order result = orderService.updateStatus(order.getId(), OrderStatus.SUCCESS.name(), token);

        assertEquals(order.getId(), result.getId());
        assertEquals(OrderStatus.SUCCESS.name(), result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateStatusError(){
        Order order = orders.get(1);
        Order newOrder = new Order();
        order.setId(order.getId());
        newOrder = builder.setCurrent(newOrder)
                .addStatus(OrderStatus.SUCCESS.name())
                .build();

        ResponseEntity<String> re = new ResponseEntity<String>("penjual1", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());
        Order result = orderService.updateStatus(order.getId(), OrderStatus.SUCCESS.name(), token);

        newOrder = builder.setCurrent(order)
                .addStatus(OrderStatus.FAILED.name())
                .build();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());
        result = orderService.updateStatus(order.getId(), OrderStatus.SUCCESS.name(), token);
        assertNull(result);

        newOrder = builder.setCurrent(order)
                .addStatus(OrderStatus.CANCELLED.name())
                .build();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());
        result = orderService.updateStatus(order.getId(), OrderStatus.SUCCESS.name(), token);
        assertNull(result);

    }

    @Test
    void testUpdateStatusInvalidStatus(){
        Order order = orders.get(1);
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId().toString());
        ResponseEntity<String> re = new ResponseEntity<String>("penjual1", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        assertThrows(IllegalArgumentException.class, ()->orderService.updateStatus(order.getId().toString(), "MEOW", token));
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    void testUpdateStatusInvalidOrderId(){
        doReturn(Optional.empty()).when(orderRepository).findById("zczc");

        ResponseEntity<String> re = new ResponseEntity<String>("penjual1", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        assertThrows(NoSuchElementException.class, ()->orderService.updateStatus("zczc", OrderStatus.SUCCESS.name(), token));
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    void testUpdateStatusInvalidUser(){
        Order order = orders.get(1);
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId().toString());

        ResponseEntity<String> re = new ResponseEntity<String>("p", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        assertNull(orderService.updateStatus(order.getId(), OrderStatus.SUCCESS.name(), token));
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    void testFindByIdIfIdFound(){
        Order order = orders.get(1);
        doReturn(Optional.of(order)).when(orderRepository).findById(order.getId());

        Order result = orderService.findById(order.getId());
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void testFindByIdIfIdNotFound(){
        doReturn(Optional.empty()).when(orderRepository).findById("zczc");

        assertNull(orderService.findById("zczc"));
    }

    @Test
    void testFindAllByAuthorIfAuthorCorrect(){
        Order order = orders.get(1);
        doReturn(orders).when(orderRepository).findAllByAuthorUsername(order.getAuthorUsername());

        List<Order> results = orderService.findAllByAuthor(order.getAuthorUsername());
        for (Order result : results){
            assertEquals(order.getAuthorUsername(), result.getAuthorUsername());
        }
        assertEquals(2, results.size());
    }

    @Test
    void testFindAllByAuthorIfAllLowerCase(){
        Order order = orders.get(1);
        doReturn(orders.stream().filter(order1 -> {return order1.getAuthorUsername().equals(order.getAuthorUsername().toLowerCase());}).collect(Collectors.toList()))
                .when(orderRepository).findAllByAuthorUsername(order.getAuthorUsername().toLowerCase());
        List<Order> results = orderService.findAllByAuthor(order.getAuthorUsername().toLowerCase());
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindAllWithSeller(){
        ResponseEntity<String> re = new ResponseEntity<String>("penjual1", HttpStatus.OK);
        HttpEntity<String> entity = createHTTPHeader();

        Session mockedSession = Mockito.mock(Session.class);
        Filter mockedFilter = Mockito.mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(mockedSession);
        when(mockedSession.enableFilter("deletedProductFilter")).thenReturn(mockedFilter);
        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(re.getBody());
        doReturn(new ArrayList<>())
                .when(orderRepository).findAllByListings_SellerUsername_AndStatus("penjual1", OrderStatus.FAILED.name());
        doReturn(new ArrayList<>())
                .when(orderRepository).findAllByListings_SellerUsername_AndStatus("penjual1", OrderStatus.SUCCESS.name());
        doReturn(new ArrayList<>())
                .when(orderRepository).findAllByListings_SellerUsername_AndStatus("penjual1", OrderStatus.CANCELLED.name());
        doReturn(orderHasSeller("penjual1"))
                .when(orderRepository).findAllByListings_SellerUsername_AndStatus("penjual1", OrderStatus.WAITINGPAYMENT.name());
        List<Order> results = orderService.findAllWithSeller(token);
        assertEquals(results, orders);
    }

    @Test
    void testFailAllWithListing(){
        Listing listing1 = listingBuilder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addSellerUsername("penjual1")
                .addQuantity(2)
                .addName("Sampo Cap Bambang")
                .build();

        Session mockedSession = Mockito.mock(Session.class);
        Filter mockedFilter = Mockito.mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(mockedSession);
        when(mockedSession.enableFilter("deletedProductFilter")).thenReturn(mockedFilter);
        orderService.failAllWithListing(listing1);
        verify(orderRepository,times(1)).findOrdersByListings_Id(listing1.getId());
    }

    private HttpEntity<String> createHTTPHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        return new HttpEntity<>("body", headers);
    }

    private List<Order> orderHasSeller(String username){
        List<Order> ordersLocal = new ArrayList<>();
        for (Order o: orders){
            for (Listing l : o.getListings()) {
                if (l.getSellerUsername().equals(username)) {
                    ordersLocal.add(o);
                    break;
                }
            }
        }
        return ordersLocal;
    }
}
