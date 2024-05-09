//package id.ac.ui.cs.advprog.afk3.repository;
//
//import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
//import id.ac.ui.cs.advprog.afk3.model.Listing;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//public class ListingRepositoryTest {
//    ListingRepository listingRepository;
//
//    ListingBuilder listingBuilder;
//
//    @BeforeEach
//    void setUp(){
//        listingBuilder = new ListingBuilder();
//    }
//
//    Listing createAndSaveListing(String name, String id, int quantity){
//        Listing listing = createListing(name,id,quantity);
//        listingRepository.save(listing);
//        listing.setId(id);
//
//        return listing;
//    }
//
//    Listing createListing(String name, String id, int quantity){
//        return listingBuilder.reset().addId(UUID.fromString(id)).addName(name).addQuantity(quantity).build();
//    }
//
//    @Test
//    void testCreateAndFind(){
//        Listing listing = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        List<Listing> listingIterator = listingRepository.findAll();
//        assertFalse(listingIterator.isEmpty());
//        Listing savedListing = listingIterator.getFirst();
//
//        assertEquals(savedListing.getId(), listing.getId());
//        assertEquals(savedListing.getName(), listing.getName());
//        assertEquals(savedListing.getQuantity(), listing.getQuantity());
//    }
//
//    @Test
//    void testFindAllIfEmpty(){
//        List<Listing> listingIterator = listingRepository.findAll();
//        assertTrue(listingIterator.isEmpty());
//    }
//
//    @Test
//    void testFindAllIfMoreThanOneListing(){
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        Listing listing2 = createAndSaveListing("Sampo Cap Usep","a0f9de45-90b1-437d-a0bf-d0821dde9096",50);
//
//        List<Listing> listingIterator = listingRepository.findAll();
//
//        assertFalse(listingIterator.isEmpty());
//        Listing savedListing = listingIterator.get(0);
//        assertEquals(listing1.getId(), savedListing.getId());
//        savedListing = listingIterator.get(1);
//        assertEquals(listing2.getId(), savedListing.getId());
//        assertTrue(listingIterator.isEmpty());
//    }
//
//    @Test
//    void testCreateAndDelete(){
//        List<Listing> listingIterator = listingRepository.findAll();
//
//        createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        assertFalse(listingIterator.isEmpty());
//        listingRepository.deleteById("eb558e9f-1c39-460e-8860-71af6af63bd6");
//        assertTrue(listingIterator.isEmpty());
//    }
//
//    @Test
//    void testCreateAndEdit(){
//        // Initializing first object
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        // Object with same id different attribute
//        Listing listing2 = createListing("Lalalalala","eb558e9f-1c39-460e-8860-71af6af63bd6",200);
//
//        Listing newList = listingRepository.getReferenceById(listing1.getId());
//        listingBuilder.reset().setCurrent(newList)
//                .addDescription(listing2.getDescription())
//                .addQuantity(listing2.getQuantity())
//                .addName(listing2.getName())
//                .build();
//
//        assertEquals(200, newList.getQuantity());
//        assertEquals("Lalalalala", newList.getName());
//    }
//
//    @Test
//    void testCreateEditDelete(){
//        List<Listing> listingIterator = listingRepository.findAll();
//
//        // Initializing first object
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        // Object with same id different attribute
//        Listing listing2 = createListing("Lalalalala","eb558e9f-1c39-460e-8860-71af6af63bd6",200);
//
//        Listing newList = listingRepository.getReferenceById(listing1.getId());
//        listingBuilder.reset().setCurrent(newList)
//                .addDescription(listing2.getDescription())
//                .addQuantity(listing2.getQuantity())
//                .addName(listing2.getName())
//                .build();
//
//        assertEquals(200, newList.getQuantity());
//        assertEquals("Lalalalala", newList.getName());
//
//        listingRepository.deleteById("eb558e9f-1c39-460e-8860-71af6af63bd6");
//        assertTrue(listingIterator.isEmpty());
//    }
//
//    @Test
//    void testEditIfNotFound(){
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        // Object with same id different attribute
//        Listing listing2 = createListing("Lalalalala","eb558eaa-1c39-460e-8860-71af6af63bd6",200);
//
//        Optional<Listing> newList = listingRepository.findById(listing1.getId());
//        try{
//            listingBuilder.reset().setCurrent(newList.get())
//                    .addDescription(listing2.getDescription())
//                    .addQuantity(listing2.getQuantity())
//                    .addName(listing2.getName())
//                    .build();
//        }catch (Exception e){
//            assertTrue(true);
//        }
//        assertNotEquals(200, listing1.getQuantity());
//        assertNotEquals("Lalalalala", listing1.getName());
//    }
//
//    @Test
//    void testDeleteIfNotFound(){
//        List<Listing> listingIterator = listingRepository.findAll();
//
//        createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//
//        listingRepository.deleteById("eb558e9f-1c39-460e-8860-aaaaaaaaaaaa");
//        assertFalse(listingIterator.isEmpty());
//
//    }
//
//    @Test
//    void testCreateGet(){
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//        Optional<Listing> result = listingRepository.findById(listing1.getId());
//        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", result.get().getId());
//        assertEquals("Sampo Cap Bambang", result.get().getName());
//        assertEquals(100, result.get().getQuantity());
//    }
//
//    @Test
//    void testCreateGetNotFound(){
//        assertFalse(listingRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd6").isPresent());
//    }
//
//    @Test
//    void testFindBySellerId(){
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//        listing1.setSellerUsername("a");
//        Listing listing2 = createAndSaveListing("Sampo Cap Bambang2","eb558e9f-1c39-460e-8860-71af6af63bd7",200);
//        listing2.setSellerUsername("a");
//        Listing listing3 = createAndSaveListing("Sampo Cap Bambang3","eb558e9f-1c39-460e-8860-71af6af63bd8",300);
//        listing3.setSellerUsername("b");
//
//        Optional<List<Listing>> result = listingRepository.findAllBySellerUsername("a");
//        assertEquals(2, result.get().size());
//        assertEquals("Sampo Cap Bambang", result.get().getFirst().getName());
//        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", result.get().getFirst().getId().toString());
//        assertEquals("Sampo Cap Bambang2", result.get().get(1).getName());
//        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd7", result.get().get(1).getId());
//    }
//
//    @Test
//    void testFindByListingIdNotFound(){
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//        Optional<Listing> listing = listingRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd7");
//        assertTrue(listing.isEmpty());
//    }
//    @Test
//    void testFindByListingIdFound(){
//        Listing listing1 = createAndSaveListing("Sampo Cap Bambang","eb558e9f-1c39-460e-8860-71af6af63bd6",100);
//        Optional<Listing> listing = listingRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd6");
//        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", listing.get().getId());
//    }
//}
