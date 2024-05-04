package id.ac.ui.cs.advprog.afk3.model;

import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ListingTest {

    ListingBuilder builder = new ListingBuilder();
    Listing listing;

    @BeforeEach
    void setUp(){
        this.listing = builder.reset()
                .addId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .addQuantity(100).addName("Sampo cap Bambang")
                .addDescription("bjirr..")
                .addSellerUsername("aa").build();
    }

    @Test
    void testGetListingId(){
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", listing.getId().toString());
    }

    @Test
    void testGetListingName(){
        assertEquals("Sampo cap Bambang", listing.getName());
    }

    @Test
    void testGetListingQuantity(){
        assertEquals(100, this.listing.getQuantity());
    }
    @Test
    void testGetListingSellerName(){
        assertEquals("aa", this.listing.getSellerUsername());
    }
    @Test
    void testGetListingDescription(){
        assertEquals("bjirr..", this.listing.getDescription());
    }

    @Test
    void testSetListingIdWithNewId(){
        UUID id = UUID.randomUUID();
        listing = builder.reset().setCurrent(listing).addId(id).build();
        assertEquals(id.toString(), listing.getId().toString());
    }

    @Test
    void testSetListingIdWithNoId(){
        listing = builder.reset().addId().build();
        assertNotNull(listing.getId());
    }
}
