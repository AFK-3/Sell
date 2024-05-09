package id.ac.ui.cs.advprog.afk3.service;


import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Service
public class ListingServiceImpl implements  ListingService{
    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ListingBuilder builder;

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
            listing = listingRepository.save(listing);
        }
        return listing;
    }

    public boolean fieldValid(Listing listing){
        return !listing.getName().isEmpty() && listing.getQuantity()>0;
    }

    @Override
    public List<Listing> findAll(){
        return listingRepository.findAll();
    }

    @Override
    public List<Listing> findAllBySellerId(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        if (owner.getBody()!=null){
            return listingRepository.findAllBySellerUsername(owner.getBody()).get();
        }
        return null;
    }

    @Override
    public Listing findById(String listingId){
        return listingRepository.findById(listingId).get();
    }

    @Override
    public Listing update (String listingId, Listing listing, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > role = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);

        if (owner.getBody()!=null && owner.getBody().equals(listing.getSellerUsername()) && isSeller(role.getBody())){
            Optional<Listing> toBeUpdated = listingRepository.findById(listingId);
            if (toBeUpdated.isPresent()){
                return builder.reset().setCurrent(toBeUpdated.get())
                        .addName(listing.getName())
                        .addQuantity(listing.getQuantity())
                        .addDescription(listing.getDescription())
                        .build();
            }
        }
        return null;
    }

    @Override
    public boolean deleteListingById(String listingId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange(authUrl+"user/get-username", HttpMethod.GET,entity ,String.class);
        Optional<Listing> listing = listingRepository.findById(listingId);
        if (listing.isPresent() && owner.getBody()!=null && owner.getBody().equals(listing.get().getSellerUsername())){
            listingRepository.deleteById(listingId);
            return true;
        }
        return false;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> deleteOrderAndPaymentWithListing(String listingId, String token) {
        Optional<Listing> listing = listingRepository.findById(listingId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            deleteOrderWithListing(listing.get());
//        ResponseEntity<String> result = restTemplate.exchange(authUrl+"payment/delete-by-listing-id/"+listingId,
//                HttpMethod.POST,entity ,
//                String.class);
            return CompletableFuture.completedFuture(true);
        }
        catch (Exception e){
            return CompletableFuture.completedFuture(false);
        }
    }

    public void deleteOrderWithListing(Listing listing){
        Optional<List<Order>> result = orderRepository.deleteOrdersByListings_Id(listing.getId());
        return;
    }

    private boolean isSeller(String role){
        if (role.equals(UserType.SELLER.name())) return true;
        else return role.equals(UserType.BUYERSELLER.name());
    }
}
