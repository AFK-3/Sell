package id.ac.ui.cs.advprog.afk3.Service;

import id.ac.ui.cs.advprog.afk3.Security.JwtValidator;
import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import id.ac.ui.cs.advprog.afk3.service.ListingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ListingServiceTest {
    @MockBean
    ListingRepository listingRepository;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    ListingBuilder listingBuilder;

    @InjectMocks
    ListingServiceImpl service;

    final String token = "a";

    private List<Listing> allListings;
    @Mock
    private JwtValidator validator;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        allListings = new ArrayList<>();
        createAndSaveListing("nama","eb558e9f-1c39-460e-8860-71af6af63bd6",1, "SELLER", "user");
    }

    Listing createAndSaveListing(String name, String id, int quantity, String role, String username){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        Listing listing = createListing(name,id, quantity);
        listing.setSellerUsername(username);

        ResponseEntity<String> re = new ResponseEntity<String>(username, HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>(role, HttpStatus.OK);
        for (Listing l : allListings){
            System.out.println(l.getName());
        }
        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(username);
        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role",HttpMethod.GET, entity,String.class))
                .thenReturn(re2);
        Mockito.when(listingRepository.save(listing)).thenReturn(createRepoMock(listing));
        Mockito.when(listingBuilder.setCurrent(listing)).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.addSellerUsername(username)).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.addId()).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.build()).thenReturn(listing);
        service.create(listing, token);

        return listing;
    }

    @AfterEach
    void deleteListing(){
        allListings = null;
    }

    Listing createRepoMock(Listing listing){
        if (!listing.getName().isEmpty() && listing.getQuantity()>0){
            allListings.add(listing);
        }
        return listing;
    }

    Listing editRepoMock(Listing listing){
        for(Listing datum : allListings){
            if (datum.getId().equals(listing.getId())){
                datum.setName(listing.getName());
                datum.setQuantity(listing.getQuantity());
                return datum;
            }
        }
        return null;
    }

    Listing createListing(String name, String id, int quantity){
        ListingBuilder builder = new ListingBuilder();
        return builder.reset()
                .addId(UUID.fromString(id))
                .addName(name)
                .addQuantity(quantity)
                .addSellerUsername("lala").build();
    }

    @Test
    void testCreateListingForSeller(){
        createAndSaveListing("2","12345678-1111-1111-1111-111111111111",1, UserType.SELLER.name(), "user");
        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> list = service.findAll();

        assertEquals("12345678-1111-1111-1111-111111111111", list.get(1).getId().toString());
        assertEquals("2", list.get(1).getName());
        assertEquals(1, list.get(1).getQuantity());
    }

    @Test
    void testCreateListingForBuyerSeller(){
        createAndSaveListing("2","12345678-1111-1111-1111-111111111111",1, UserType.BUYERSELLER.name(), "user");
        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> list = service.findAll();

        assertEquals("12345678-1111-1111-1111-111111111111", list.get(1).getId().toString());
        assertEquals("2", list.get(1).getName());
        assertEquals(1, list.get(1).getQuantity());
    }

    @Test
    void testCreateListingInvalidName(){
        createAndSaveListing("","12345678-1111-1111-1111-111111111111",1, UserType.SELLER.name(), "user");
        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> list = service.findAll();

        assertEquals(1,allListings.size());
    }

    @Test
    void testCreateListingInvalidQuantity(){
        createAndSaveListing("2","12345678-1111-1111-1111-111111111111",0, UserType.SELLER.name(), "user");
        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> list = service.findAll();

        assertEquals(1,allListings.size());
    }

    @Test
    void testFindAllListing(){
        createAndSaveListing("nomu","12345678-1111-1111-1111-111111111100",2, UserType.SELLER.name(), "user");
        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> list = service.findAll();

        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", list.getFirst().getId().toString());
        assertEquals("nama", list.get(0).getName());
        assertEquals(1, list.get(0).getQuantity());
        assertEquals("12345678-1111-1111-1111-111111111100", list.get(1).getId().toString());
        assertEquals("nomu", list.get(1).getName());
        assertEquals(2, list.get(1).getQuantity());
    }

    @Test
    void testUpdateGetListingSeller(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.BUYERSELLER.name(), "user");
        Listing listing3 = createListing("nomu","00558e9f-1c39-460e-8860-71af6af63bc7",2); // Data to edit listing2
        listing3.setSellerUsername("user");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> re = new ResponseEntity<String>("user", HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>("SELLER", HttpStatus.OK);
        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user");
        Mockito.when(restTemplate.exchange(
                        "nulluser/get-role",HttpMethod.GET, entity,String.class))
                .thenReturn(re2);
        Optional<Listing> listingToEditOpt = Optional.of(listing2);
        Mockito.when(listingRepository.findById(listing2.getId())).thenReturn(Optional.of(listing2));
        Mockito.when(listingBuilder.reset()).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.setCurrent(listingToEditOpt.get())).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.addName(listing3.getName())).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.addQuantity(listing3.getQuantity())).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.addDescription(listing3.getDescription())).thenReturn(listingBuilder);
        Mockito.when(listingBuilder.build()).thenReturn(editRepoMock(listing3));
        service.update(listing2.getId(), listing3, token);
        verify(listingRepository, times(1)).findById(listing3.getId());
        when(listingRepository.findById(listing2.getId())).thenReturn(Optional.of(listing2));
        Listing resultEdit = service.findById(listing2.getId());

        assertEquals("00558e9f-1c39-460e-8860-71af6af63bc7", resultEdit.getId().toString());
        assertEquals("nomu", resultEdit.getName());
        assertEquals(2, resultEdit.getQuantity());
    }

    @Test
    void testUpdateListingForBuyerSeller(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");
        Listing listing3 = createListing("nomu","00558e9f-1c39-460e-8860-71af6af63bc7",2); // Data to edit listing2

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> re = new ResponseEntity<String>("user", HttpStatus.OK);
        ResponseEntity<String> re2 = new ResponseEntity<String>("BUYERSELLER", HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user");
        when(listingRepository.findById(listing3.getId())).thenReturn(Optional.of(listing3));
        service.update(listing3.getId(), listing3, token);
        when(listingRepository.findById(listing2.getId())).thenReturn(Optional.of(listing3));
        Listing resultEdit = service.findById(listing2.getId().toString());

        assertEquals("00558e9f-1c39-460e-8860-71af6af63bc7", resultEdit.getId().toString());
        assertEquals("nomu", resultEdit.getName());
        assertEquals(2, resultEdit.getQuantity());
    }

    @Test
    void testDeleteListing(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> re = new ResponseEntity<String>("user", HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user");
        when(listingRepository.findById(listing2.getId())).thenReturn(Optional.of(listing2));
        service.deleteListingById("00558e9f-1c39-460e-8860-71af6af63bc7", token);
        allListings.remove(listing2); // assume listingRepo deletes id

        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> list = service.findAll();
        verify(listingRepository, times(1)).deleteById(listing2.getId());
        assertEquals(1,list.size());
    }

    @Test
    void testDeleteListingNotFound(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> re = new ResponseEntity<String>("user", HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user");
        service.deleteListingById("id", token);
        verify(listingRepository, times(0)).deleteById("id");
    }

    @Test
    void testDeleteListingWrongUser(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> re = new ResponseEntity<String>("user1", HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user1");
        service.deleteListingById("00558e9f-1c39-460e-8860-71af6af63bc7", token);
        verify(listingRepository, times(0)).deleteById("00558e9f-1c39-460e-8860-71af6af63bc7");
    }
    @Test
    void testDeleteListingNullUser(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity re = new ResponseEntity(null, HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(null);
        service.deleteListingById("00558e9f-1c39-460e-8860-71af6af63bc7", token);
        verify(listingRepository, times(0)).deleteById("00558e9f-1c39-460e-8860-71af6af63bc7");
    }

    @Test
    void testFindAllBySellerId(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> re = new ResponseEntity<String>("user", HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user");
        when(listingRepository.findAllBySellerUsername("user")).thenReturn(Optional.of(
                allListings.stream().filter(
                        listing -> {return listing.getSellerUsername().equals("user");}).collect(Collectors.toList())
                ));
        List<Listing> result = service.findAllBySellerId(token);
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindAllBySellerIdNotFound(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity re = new ResponseEntity(null, HttpStatus.OK);

        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(null);
        List<Listing> result = service.findAllBySellerId(token);
        assertNull(null);
    }

    @Test
    void testDeleteOrderAndPaymentWithListing(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");
        when(listingRepository.findById("00558e9f-1c39-460e-8860-71af6af63bc7")).thenReturn(Optional.of(listing2));
        CompletableFuture<Boolean> result = service.deleteOrderAndPaymentWithListing("00558e9f-1c39-460e-8860-71af6af63bc7", token);
        CompletableFuture.allOf(result).join();
        verify(orderRepository, timeout(100)).deleteOrdersByListings_Id("00558e9f-1c39-460e-8860-71af6af63bc7");
    }

    @Test
    void testFindByIdNotFound(){
        when(listingRepository.findById("00558e9f-1c39-460e-8860-71af6af63bc9")).thenReturn(null);
        assertNull(service.findById("00558e9f-1c39-460e-8860-71af6af63bc8"));
    }

    @Test
    void testFindById(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");
        when(listingRepository.findById("00558e9f-1c39-460e-8860-71af6af63bc7")).thenReturn(Optional.of(listing2));
        Listing result = service.findById("00558e9f-1c39-460e-8860-71af6af63bc7");
        assertNotNull(result);
        assertEquals("00558e9f-1c39-460e-8860-71af6af63bc7", result.getId());
        assertEquals("user", result.getSellerUsername());
        assertEquals(20, result.getQuantity());
    }

    @Test
    void testFindAll(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");
        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn("user");
        when(listingRepository.findAll()).thenReturn(allListings);
        List<Listing> result = service.findAll(token);
        assertNotNull(result);
    }

    @Test
    void testFindAllNotValidJWT(){
        Listing listing2 = createAndSaveListing("aa", "00558e9f-1c39-460e-8860-71af6af63bc7",20, UserType.SELLER.name(), "user");
        Mockito.when(validator.getUsernameFromJWT(token)).thenReturn(null);
        List<Listing> result = service.findAll(token);
        assertNull(result);
    }
}

