package id.ac.ui.cs.advprog.afk3.Enum;

import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderStatusTest {
    @Test
    void testSeller(){
        assertEquals("FAILED", OrderStatus.FAILED.name());
    }

    @Test
    void testBuyer(){
        assertEquals("SUCCESS", OrderStatus.SUCCESS.name());
    }

    @Test
    void testBuyerSeller(){
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }

    @Test
    void testStaff(){
        assertEquals("WAITINGPAYMENT", OrderStatus.WAITINGPAYMENT.name());
    }

    @Test
    void testContainsSuccessful(){
        assertTrue(OrderStatus.contains("FAILED"));
        assertTrue(OrderStatus.contains("CANCELLED"));
        assertTrue(OrderStatus.contains("SUCCESS"));
        assertTrue(OrderStatus.contains("WAITINGPAYMENT"));
        assertFalse(OrderStatus.contains("A"));
    }
}
