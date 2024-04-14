package id.ac.ui.cs.advprog.afk3.repository;

import id.ac.ui.cs.advprog.afk3.model.Builder.ListingBuilder;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Repository
public class ListingRepository {
    private final List<Listing> listingData = new ArrayList<>();

    @Autowired
    private ListingBuilder listingBuilder;
    public Listing createListing(Listing listing){
        listing = listingBuilder.reset().setCurrent(listing).addId().build();
        listingData.add(listing);
        return listing;
    }

    public Iterator<Listing> findAll(){
        return listingData.iterator();
    }

    public Listing findById(String id){
        for (Listing Listing: listingData){
            if (Listing.getId().equals(UUID.fromString(id))){
                return Listing;
            }
        }
        return null;
    }

    public Listing update(String id, Listing updatedListing){
        for (int i=0; i<listingData.size(); i++){
            Listing listing = listingData.get(i);
            if(listing.getId().equals(UUID.fromString(id))){
                Listing newListing = listingBuilder.reset()
                        .setCurrent(updatedListing)
                        .addId(UUID.fromString(id))
                        .addSellerUsername(listing.getSellerUsername())
                        .build();
                newListing.setId(UUID.fromString(id));
                listingData.remove(i);
                listingData.add(i,newListing);
                return newListing;
            }
        }
        return null;
    }

    public void delete(String id){
        listingData.removeIf(Listing -> Listing.getId().equals(UUID.fromString(id)));
    }
}
