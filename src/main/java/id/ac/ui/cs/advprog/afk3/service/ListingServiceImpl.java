package id.ac.ui.cs.advprog.afk3.service;


import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.repository.ListingRepository;
import id.ac.ui.cs.advprog.afk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Listing create(Listing listing){
        User owner = userRepository.findByUsername(listing.getSellerUsername());
        if(ownerValid(owner) && fieldValid(listing)){
            listingRepository.createListing(listing);

            return listing;
        }
        return null;
    }

    public boolean fieldValid(Listing listing){
        return listing.getName().length()>0 && listing.getQuantity()>0;
    }

    public boolean ownerValid(User owner){
        return owner!=null
                && (owner.getType().equals(UserType.SELLER.name())
                || owner.getType().equals(UserType.BUYERSELLER.name()))
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
    public Listing update (String listingId, Listing listing){
        return listingRepository.update(listingId, listing);
    }

    @Override
    public void deleteListingById(String listingId) {
        listingRepository.delete(listingId);
    }
}
