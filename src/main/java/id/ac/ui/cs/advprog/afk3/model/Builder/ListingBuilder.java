package id.ac.ui.cs.advprog.afk3.model.Builder;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ListingBuilder {
    private Listing currentListing;
    public ListingBuilder(){
        this.reset();
    }

    public ListingBuilder reset(){
        currentListing = new Listing();
        return this;
    }

    public ListingBuilder addName(String name){
        currentListing.setName(name);
        return this;
    }

    public ListingBuilder addQuantity(int quantity){
        currentListing.setQuantity(quantity);
        return this;
    }

    public ListingBuilder addDescription(String description){
        currentListing.setDescription(description);
        return this;
    }

    public ListingBuilder addId(){
        currentListing.setId(UUID.randomUUID().toString());
        return this;
    }

    public ListingBuilder addId(UUID id){
        currentListing.setId(id.toString());
        return this;
    }

    public ListingBuilder addSellerUsername(String username){
        currentListing.setSellerUsername(username);
        return this;
    }

    public ListingBuilder setCurrent(Listing listing){
        currentListing = listing;
        return this;
    }

    public Listing build(){
        return currentListing;
    }
}
