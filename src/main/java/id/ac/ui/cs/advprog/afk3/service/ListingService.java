package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.Listing;

import java.util.List;

public interface ListingService {
    public Listing create(Listing listing, String token);
    public List<Listing> findAll();
    Listing findById(String listingId);
    public Listing update(String listingId, Listing listing);
    public void deleteListingById(String listingId);
}
