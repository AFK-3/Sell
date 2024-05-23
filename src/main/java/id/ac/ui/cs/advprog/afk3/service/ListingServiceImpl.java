package id.ac.ui.cs.advprog.afk3.service;


import id.ac.ui.cs.advprog.afk3.Security.JwtValidator;
import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class ListingServiceImpl implements  ListingService{
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ListingBuilder builder;

    @Value("${app.auth-domain}")
    String authUrl;

    @Autowired
    JwtValidator validator;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Listing create(Listing listing, String token){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        String owner = validator.getUsernameFromJWT(token);
        log.info("hitting url "+authUrl+"user/get-role");
        ResponseEntity<String > role = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);

        if (owner!=null && fieldValid(listing) && isSeller(role.getBody())){
            listing = builder.setCurrent(listing).addSellerUsername(owner).addId().build();
            log.info("listing {} saved SUCCESS",listing.getId());
            listing = listingRepository.save(listing);

        }else{
            log.info("listing {} saved FAILED",listing.getId());
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

        String owner = validator.getUsernameFromJWT(token);
        if (owner!=null){
            log.info("Find listings by username {} SUCCESSFUL", owner);
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter("deletedProductFilter");
            filter.setParameter("isDeleted", false);
            Optional<List<Listing>> res =  listingRepository.findAllBySellerUsername(owner);
            session.disableFilter("deletedProductFilter");
            if (res.isPresent()){
                return res.get();
            }
        }
        log.error("Find listings by username FAILED, INVALID Token");
        return null;
    }

    @Override
    public Listing findById(String listingId){
        Optional<Listing> listing = listingRepository.findById(listingId);
        if (listing.isPresent()){
            log.info("Find listings by id {} SUCCESSFUL", listingId);
            return listing.get();
        }
        log.error("Find listings by id {} FAILED, id not found", listingId);
        return null;
    }

    @Override
    public Listing update (String listingId, Listing listing, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        String owner = validator.getUsernameFromJWT(token);
        log.info("hitting url "+authUrl+"user/get-role");
        ResponseEntity<String > role = restTemplate.exchange(authUrl+"user/get-role", HttpMethod.GET,entity ,String.class);

        if (owner!=null ){
            if (owner.equals(listing.getSellerUsername())){
                if(isSeller(role.getBody())){
                    Optional<Listing> toBeUpdated = listingRepository.findById(listingId);
                    if (toBeUpdated.isPresent()) {
                        log.info("Update listing by id {} SUCCESSFUL", listingId);
                        return listingRepository.save(builder.reset().setCurrent(toBeUpdated.get())
                                .addName(listing.getName())
                                .addQuantity(listing.getQuantity())
                                .addDescription(listing.getDescription())
                                .build());
                    }
                }
            }
        }
        log.info("Update listing by id {} FAILED", listingId);
        return null;
    }

    @Override
    public boolean deleteListingById(String listingId, String token) {

        String owner = validator.getUsernameFromJWT(token);

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", true);
        Optional<Listing> listing = listingRepository.findById(listingId);

        if (listing.isPresent() && !listing.get().isDeleted() && owner!=null && owner.equals(listing.get().getSellerUsername())){
            log.info("Delete listing by id {} SUCCESSFUL", listingId);
            listingRepository.deleteById(listingId);
            return true;
        }
        log.error("Delete listing by id {} FAILED", listingId);
        return false;
    }

    @Override
    public List<Listing> findAll(String token) {
        String owner = validator.getUsernameFromJWT(token);

        if (owner!=null){
            return listingRepository.findAll();
        }
        log.error("FindAll listing not allowed");
        return null;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Boolean> failOrderWithListing(String listingId, String token) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", true);
        Optional<Listing> listing = listingRepository.findById(listingId);
        session.disableFilter("deletedProductFilter");
        listing.ifPresent(this::failOrderWithListing);
        return CompletableFuture.completedFuture(true);
    }

    public void failOrderWithListing(Listing listing){
        orderService.failAllWithListing(listing);
    }

    private boolean isSeller(String role){
        if (role.equals(UserType.SELLER.name())) return true;
        else return role.equals(UserType.BUYERSELLER.name());
    }
}
