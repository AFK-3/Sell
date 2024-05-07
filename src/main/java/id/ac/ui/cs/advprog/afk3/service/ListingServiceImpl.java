package id.ac.ui.cs.advprog.afk3.service;


import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class ListingServiceImpl implements  ListingService{
    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${app.auth-domain}")
    String authUrl;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Listing create(Listing listing, String token){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > role = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);
        System.out.println("zczc"+owner+" "+role);
        if (owner.getBody()!=null && fieldValid(listing) && owner.getBody().equals(listing.getSellerUsername()) && isSeller(role.getBody())){
            listing = listingRepository.createListing(listing);
        }
        return listing;
    }

    public boolean fieldValid(Listing listing){
        return !listing.getName().isEmpty() && listing.getQuantity()>0;
    }

    @Override
    public List<Listing> findAll(){
        Iterator<Listing> listingIterator=listingRepository.findAll();
        List<Listing> allListing = new ArrayList<>();
        listingIterator.forEachRemaining(allListing::add);
        return allListing;
    }

    @Override
    public List<Listing> findAllBySellerId(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        if (owner.getBody()!=null){
            return listingRepository.findBySellerId(owner.getBody());
        }
        return null;
    }

    @Override
    public Listing findById(String listingId){
        return listingRepository.findById(listingId);
    }

    @Override
    public Listing update (String listingId, Listing listing, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > role = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);

        if (owner.getBody()!=null && owner.getBody().equals(listing.getSellerUsername()) && isSeller(role.getBody())){
            return listingRepository.update(listingId, listing);
        }
        return null;
    }

    @Override
    public boolean deleteListingById(String listingId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        Listing listing = listingRepository.findById(listingId);
        if (listing != null && owner.getBody()!=null && owner.getBody().equals(listing.getSellerUsername())){
            deleteOrderAndPaymentWithListing(listingRepository.findById(listingId),token);
            listingRepository.delete(listingId);
            return true;
        }
        return false;
    }

    @Override
    @Async
    public void deleteOrderAndPaymentWithListing(Listing listing, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String listingId = listing.getId().toString();
//        ResponseEntity<String> owner = restTemplate.exchange(authUrl+"payment/delete-by-listing-id/"+listingId,
//                HttpMethod.POST,entity ,
//                String.class);
        deleteOrderWithListing(listing);
    }

    @Async
    void deleteOrderWithListing(Listing listing){
        orderRepository.deleteAllWithListing(listing);
    }

    private boolean isSeller(String role){
        if (role.equals(UserType.SELLER.name())) return true;
        else return role.equals(UserType.BUYERSELLER.name());
    }
}
