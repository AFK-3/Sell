package id.ac.ui.cs.advprog.afk3.Enum;

import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserTypeTest {
    @Test
    void testSeller(){
        assertEquals("SELLER", UserType.SELLER.name());
    }

    @Test
    void testBuyer(){
        assertEquals("BUYER", UserType.BUYER.name());
    }

    @Test
    void testBuyerSeller(){
        assertEquals("BUYERSELLER", UserType.BUYERSELLER.name());
    }

    @Test
    void testStaff(){
        assertEquals("STAFF", UserType.STAFF.name());
    }

    @Test
    void testContainsSuccessful(){
        assertTrue(UserType.contains("SELLER"));
        assertTrue(UserType.contains("BUYER"));
        assertTrue(UserType.contains("BUYERSELLER"));
        assertTrue(UserType.contains("STAFF"));
        assertFalse(UserType.contains("A"));
    }

    @Test
    void testGetAll(){
        assertTrue(UserType.getAll().contains("SELLER"));
        assertTrue(UserType.getAll().contains("BUYER"));
        assertTrue(UserType.getAll().contains("BUYERSELLER"));
        assertTrue(UserType.getAll().contains("STAFF"));
        assertFalse(UserType.getAll().contains("A"));
    }
}
