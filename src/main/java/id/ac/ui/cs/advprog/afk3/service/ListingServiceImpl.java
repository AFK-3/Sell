package id.ac.ui.cs.advprog.afk3.service;


import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class ListingServiceImpl implements  ListingService{
    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Listing create(Listing listing, String token){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);


        ResponseEntity<String > owner = restTemplate.exchange("http://35.198.243.155/user/get-username", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > role = restTemplate.exchange("http://35.198.243.155/user/get-role", HttpMethod.GET,entity ,String.class);
        if (owner.getBody()!=null && fieldValid(listing) && owner.getBody().equals(listing.getSellerUsername()) &&
        isSeller(role.getBody())){
            listingRepository.createListing(listing);
        }
        return null;
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
    public Listing findById(String listingId){
        return listingRepository.findById(listingId);
    }

    @Override
    public Listing update (String listingId, Listing listing, String token){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange("http://35.198.243.155/user/get-username", HttpMethod.GET,entity ,String.class);
        ResponseEntity<String > role = restTemplate.exchange("http://35.198.243.155/user/get-role", HttpMethod.GET,entity ,String.class);
        if (owner.getBody()!=null && owner.getBody().equals(listing.getSellerUsername()) &&
                isSeller(role.getBody())){
            return listingRepository.update(listingId, listing);
        }
        return null;
    }

    @Override
    public void deleteListingById(String listingId, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String > owner = restTemplate.exchange("http://35.198.243.155/user/get-username", HttpMethod.GET,entity ,String.class);
        Listing listing = listingRepository.findById(listingId);
        if (listing != null && owner.getBody()!=null && owner.getBody().equals(listing.getSellerUsername())){
            listingRepository.delete(listingId);
        }
    }

    private boolean isSeller(String role){
        return UserType.BUYERSELLER.name().equals(role) ||
                UserType.SELLER.name().equals(role);
    }
}
