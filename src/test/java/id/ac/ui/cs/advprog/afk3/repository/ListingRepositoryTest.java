package id.ac.ui.cs.advprog.afk3.repository;

import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ListingRepositoryTest {
    @InjectMocks
    ListingRepository listingRepository;

    ListingBuilder listingBuilder;

    @BeforeEach
    void setUp(){
        listingBuilder = new ListingBuilder();
    }

    Listing createAndSaveListing(String name, String id, int quantity){
        Listing listing = createListing(name,id,quantity);
        listingRepository.createListing(listing);
        listing.setId(UUID.fromString(id));

        return listing;
    }

    Listing createListing(String name, String id, int quantity){
        return listingBuilder.reset().addId(UUID.fromString(id)).addName(name).addQuantity(quantity).build();
    }

    @Test
    void testCreateAndFind(){
        Listing listing = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        Iterator<Listing> listingIterator = listingRepository.findAll();
        assertTrue(listingIterator.hasNext());
        Listing savedListing = listingIterator.next();

        assertEquals(savedListing.getId(), listing.getId());
        assertEquals(savedListing.getName(), listing.getName());
        assertEquals(savedListing.getQuantity(), listing.getQuantity());
    }

    @Test
    void testFindAllIfEmpty(){
        Iterator<Listing> listingIterator = listingRepository.findAll();
        assertFalse(listingIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOneListing(){
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        Listing listing2 = createAndSaveListing("Sampo Cap Usep","a0f9de45-90b1-437d-a0bf-d0821dde9096",50);

        Iterator<Listing> listingIterator = listingRepository.findAll();

        assertTrue(listingIterator.hasNext());
        Listing savedListing = listingIterator.next();
        assertEquals(listing1.getId(), savedListing.getId());
        savedListing = listingIterator.next();
        assertEquals(listing2.getId(), savedListing.getId());
        assertFalse(listingIterator.hasNext());
    }

    @Test
    void testCreateAndDelete(){
        Iterator<Listing> listingIterator = listingRepository.findAll();

        createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        assertTrue(listingIterator.hasNext());
        listingRepository.delete("eb558e9f-1c39-460e-8860-71af6af63bd6");
        assertFalse(listingIterator.hasNext());
    }

    @Test
    void testCreateAndEdit(){
        // Initializing first object
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        // Object with same id different attribute
        Listing listing2 = createListing("Lalalalala","eb558e9f-1c39-460e-8860-71af6af63bd6",200);

        Listing newList = listingRepository.update(listing1.getId().toString(),listing2);

        assertEquals(200, newList.getQuantity());
        assertEquals("Lalalalala", newList.getName());
    }

    @Test
    void testCreateEditDelete(){
        Iterator<Listing> listingIterator = listingRepository.findAll();

        // Initializing first object
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        // Object with same id different attribute
        Listing listing2 = createListing("Lalalalala","eb558e9f-1c39-460e-8860-71af6af63bd6",200);

        Listing newList = listingRepository.update(listing1.getId().toString(),listing2);

        assertEquals(200, newList.getQuantity());
        assertEquals("Lalalalala", newList.getName());

        listingRepository.delete("eb558e9f-1c39-460e-8860-71af6af63bd6");
        assertFalse(listingIterator.hasNext());
    }

    @Test
    void testEditIfNotFound(){
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        // Object with same id different attribute
        Listing listing2 = createListing("Lalalalala","eb558eaa-1c39-460e-8860-71af6af63bd6",200);

        listingRepository.update(listing2.getId().toString(), listing2);

        assertNotEquals(200, listing1.getQuantity());
        assertNotEquals("Lalalalala", listing1.getName());
    }

    @Test
    void testDeleteIfNotFound(){
        Iterator<Listing> listingIterator = listingRepository.findAll();

        createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);

        listingRepository.delete("eb558e9f-1c39-460e-8860-aaaaaaaaaaaa");
        assertTrue(listingIterator.hasNext());

    }

    @Test
    void testCreateGet(){
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
        Listing result = listingRepository.findById(listing1.getId().toString());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", result.getId().toString());
        assertEquals("Sampo Cap Bambang", result.getName());
        assertEquals(100, result.getQuantity());
    }

    @Test
    void testCreateGetNotFound(){
        Listing listing = listingRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd6");
        assertNull(listing);
    }

    @Test
    void testFindBySellerId(){
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
        listing1.setSellerUsername("a");
        Listing listing2 = createAndSaveListing("Sampo Cap Bambang2","eb558e9f-1c39-460e-8860-71af6af63bd7",200);
        listing2.setSellerUsername("a");
        Listing listing3 = createAndSaveListing("Sampo Cap Bambang3","eb558e9f-1c39-460e-8860-71af6af63bd8",300);
        listing3.setSellerUsername("b");

        List<Listing> result = listingRepository.findBySellerId("a");
        assertEquals(2, result.size());
        assertEquals("Sampo Cap Bambang", result.getFirst().getName());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", result.getFirst().getId().toString());
        assertEquals("Sampo Cap Bambang2", result.get(1).getName());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd7", result.get(1).getId().toString());
    }

    @Test
    void testFindByListingIdNotFound(){
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
        Listing listing = listingRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd7");
        assertNull(listing);
    }
    @Test
    void testFindByListingIdFound(){
        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
        Listing listing = listingRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd6");
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", listing.getId().toString());
    }
}
