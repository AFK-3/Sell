package id.ac.ui.cs.advprog.afk3.model;

import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Builder.OrderBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderTest {

    ListingBuilder listingBuilder = new ListingBuilder();

    OrderBuilder orderBuilder = new OrderBuilder();
    private List<Listing> products;

    @BeforeEach
    void setUp(){
        this.products = new ArrayList<>();
        Listing listing1 = listingBuilder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addQuantity(100).addName("Sampo Cap Bambang")
                .addDescription("bjirr..")
                .addSellerUsername("aa").build();
        Listing listing2 = listingBuilder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addQuantity(100).addName("Sampo Cap Usep")
                .addDescription("awokwok..")
                .addSellerUsername("ab").build();
        this.products.add(listing1);
        this.products.add(listing2);
    }

    @Test
    void testCreateOrderWithIDOnly(){
        Order order = new Order();
        UUID id = UUID.randomUUID();
        order.setId(id);
        assertEquals(id, order.getId());
    }

    @Test
    void testCreateOrderWithTimeOnly(){
        Order order = new Order();

        order.setOrderTime(Long.valueOf("10"));
        assertEquals(10, order.getOrderTime());
    }

    @Test
    void testCreateOrderEmptyProduct(){
        this.products.clear();

        assertThrows(IllegalArgumentException.class, ()->{
            Order order = orderBuilder.reset().firstSetUp()
                    .addListings(this.products)
                    .addAuthor("Safira Sudarajat")
                    .build();
        });
    }

    @Test
    void testCreateOrderDefaultStatus(){
        Order order = orderBuilder.reset().firstSetUp()
                .addListings(this.products)
                .addAuthor("Safira Sudarajat")
                .build();

        assertSame(this.products, order.getListings());
        assertEquals(2, order.getListings().size());
        assertEquals("Sampo Cap Bambang", order.getListings().get(0).getName());
        assertEquals("Sampo Cap Usep", order.getListings().get(1).getName());

        assertEquals("Safira Sudarajat", order.getAuthorUsername());
        assertEquals("WAITINGPAYMENT", order.getStatus());
    }

    @Test
    void testOrderCreateOrderSuccessfulStatus(){
        Order order = orderBuilder.reset().firstSetUp()
                .addListings(this.products)
                .addAuthor("Safira Sudarajat")
                .addStatus(OrderStatus.SUCCESS.name())
                .build();
        assertEquals("SUCCESS", order.getStatus());
    }

    @Test
    void testOrderCreateOrderInvalidStatus(){
        assertThrows(IllegalArgumentException.class, ()->{
            Order order = orderBuilder.reset().firstSetUp()
                    .addListings(this.products)
                    .addAuthor("Safira Sudarajat")
                    .addStatus("why??!!")
                    .build();
        });
    }

    @Test
    void testSetStatusToCancelled(){
        Order order = orderBuilder.reset().firstSetUp()
                .addListings(this.products)
                .addAuthor("Safira Sudarajat")
                .addStatus(OrderStatus.CANCELLED.name())
                .build();
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    void testSetStatusToFailed(){
        Order order = orderBuilder.reset().firstSetUp()
                .addListings(this.products)
                .addAuthor("Safira Sudarajat")
                .addStatus(OrderStatus.FAILED.name())
                .build();
        assertEquals("FAILED", order.getStatus());
    }

    @Test
    void testSetCurrent(){
        Order order = orderBuilder.reset().firstSetUp()
                .addListings(this.products)
                .addAuthor("Safira Sudarajat")
                .addStatus(OrderStatus.FAILED.name())
                .build();
        Order order2 = orderBuilder.reset().setCurrent(order).addAuthor("aaa").build();
        assertEquals("aaa", order2.getAuthorUsername());
    }


}
