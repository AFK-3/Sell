package id.ac.ui.cs.advprog.afk3.repository;

import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderRepositoryTest {
    OrderRepository orderRepository;
    List<Order> orders;

    @BeforeEach
    void setUp(){
        orderRepository = new OrderRepository();
        ListingBuilder builder = new ListingBuilder();
        Listing listing1 = builder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addQuantity(100).addName("Sampo cap Bambang")
                .addDescription("bjirr..")
                .addSellerUsername("aa").build();

        List<Listing> listings = new ArrayList<>();
        listings.add(listing1);

        OrderBuilder orderBuilder = new OrderBuilder();

        orders = new ArrayList<>();
        Order order1 = orderBuilder.reset().firstSetUp()
                .addListings(listings)
                .addAuthor("Pemilik1")
                .build();
        orders.add(order1);
        Order order2 = orderBuilder.reset().firstSetUp()
                .addListings(listings)
                .addAuthor("Pemilik2")
                .build();
        orders.add(order2);
        Order order3 = orderBuilder.reset().firstSetUp()
                .addListings(listings)
                .addAuthor("Pemilik3")
                .build();
        orders.add(order3);
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
    }

    @Test
    void testSaveCreate(){
        Order order = orders.get(1);
        Order result = orderRepository.save(order);

        Order findResult = orderRepository.findById(orders.get(1).getId().toString());
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getId(), findResult.getId());
        assertEquals(order.getOrderTime(), findResult.getOrderTime());
        assertEquals(order.getAuthorUsername(), findResult.getAuthorUsername());
        assertEquals(order.getStatus(), findResult.getStatus());
    }

    @Test
    void testSaveUpdate(){
        Order order = orders.get(1);
        orderRepository.save(order);

        OrderBuilder orderBuilder = new OrderBuilder();
        Order newOrder = new Order();
        newOrder = orderBuilder.reset().firstSetUp().setCurrent(newOrder).addAuthor("bukan pemilik 2").build();
        newOrder.setId(order.getId());
        newOrder.setOrderTime(order.getOrderTime());
        Order result = orderRepository.save(newOrder);

        Order findResult = orderRepository.findById(orders.get(1).getId().toString());
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getId(), findResult.getId());
        assertEquals(order.getOrderTime(), findResult.getOrderTime());
        assertEquals("bukan pemilik 2", findResult.getAuthorUsername());
    }

    @Test
    void testFindByIdIfIdFound(){
        for (Order order : orders){
            orderRepository.save(order);
        }

        Order findResult = orderRepository.findById(orders.get(1).getId().toString());
        assertEquals(orders.get(1).getId(), findResult.getId());
        assertEquals(orders.get(1).getOrderTime(), findResult.getOrderTime());
        assertEquals(orders.get(1).getAuthorUsername(), findResult.getAuthorUsername());
        assertEquals(orders.get(1).getStatus(), findResult.getStatus());
    }

    @Test
    void testFindByIdIfIdNotFound(){
        for (Order order : orders){
            orderRepository.save(order);
        }
        Order findResult = orderRepository.findById("zczc");
        assertNull(findResult);
    }

    @Test
    void testFindAllByAuthorIfAuthorCorrect(){
        for (Order order : orders){
            orderRepository.save(order);
        }
        List<Order> orderList = orderRepository.findAllByAuthor(orders.get(1).getAuthorUsername());
        assertEquals(1, orderList.size());
    }

    @Test
    void testFindAllByAuthorIfAllLowerCase(){
        for (Order order : orders){
            orderRepository.save(order);
        }
        List<Order> orderList = orderRepository.findAllByAuthor(orders.get(1).getAuthorUsername().toLowerCase());
        assertTrue(orderList.isEmpty());
    }

    @Test
    void testFindAllBySeller(){
        for (Order order : orders){
            orderRepository.save(order);
        }
        List<Order> orderList = orderRepository.findAllWithSeller("aa");
        assertEquals(3, orderList.size());
    }

    @Test
    void testFindAllBySellerIfAllUpperCase(){
        for (Order order : orders){
            orderRepository.save(order);
        }
        List<Order> orderList = orderRepository.findAllWithSeller("aa".toUpperCase());
        assertEquals(0, orderList.size());
    }

    @Test
    void testDeleteAllWithListing(){
        ListingBuilder builder = new ListingBuilder();
        Listing listing = builder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addQuantity(100).addName("Sampo cap Bambang")
                .addDescription("bjirr..")
                .addSellerUsername("aa").build();
        orderRepository.deleteAllWithListing(listing);
        Iterator<Order> it = orderRepository.findAll();
        assertFalse(it.hasNext());
    }

    @Test
    void testDeleteAllWithListingNotFound(){
        ListingBuilder builder = new ListingBuilder();
        Listing listing = builder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd7"))
                .addQuantity(100).addName("Sampo cap Bambang")
                .addSellerUsername("aa").build();
        orderRepository.deleteAllWithListing(listing);
        Iterator<Order> it = orderRepository.findAll();
        assertNotNull(it.next());
        assertNotNull(it.next());
        assertNotNull(it.next());
    }

}
