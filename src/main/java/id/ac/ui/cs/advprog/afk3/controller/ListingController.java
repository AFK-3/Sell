package id.ac.ui.cs.advprog.afk3.controller;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/listing")
public class ListingController {
    String listHTML = "listingList";
    String createHTML = "listingCreate";
    String editHTML = "listingEdit";

    @Autowired
    private ListingService listingService;

    @GetMapping("/create")
    public ModelAndView createListingPage(){
        Listing listing = new Listing();
        ModelAndView modelAndView = getModelAndView(createHTML);
        modelAndView.addObject("listing", listing);
        return modelAndView;
    }

    @PostMapping("/create")
    public ResponseEntity<Listing> createListingPost(@ModelAttribute("product") Listing listing, @RequestHeader("Authorization") String token){
        Listing newlisting = listingService.create(listing, token);
        return new ResponseEntity<>(newlisting, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ModelAndView listingListPage(Model model){
        List<Listing> allListings = listingService.findAll();
        ModelAndView modelAndView = getModelAndView(listHTML);
        modelAndView.addObject("listings", allListings);
        return modelAndView;
    }

    @PostMapping("/edit")
    public ResponseEntity<Listing> editProductPost(@ModelAttribute("listing") Listing listing, @RequestHeader("Authorization") String token){
        listingService.update(listing.getId().toString(), listing ,token);
        return new ResponseEntity<>(listing, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteListing(Model model, @RequestParam("listingId") String listingId, @RequestHeader("Authorization") String token){
        CompletableFuture<Boolean> resultFromDelete = listingService.deleteOrderAndPaymentWithListing(listingId, token);
        boolean success = listingService.deleteListingById(listingId,token);
        if (success){
            return new ResponseEntity<>("Listing with ID: "+listingId+" deleted successfully!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Listing with ID: "+listingId+" FAILED to be deleted!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get-by-id/{listingId}")
    public ResponseEntity<Listing> getById(Model model, @PathVariable("listingId") String listingId, @RequestHeader("Authorization") String token) {
        Listing foundListing = listingService.findById(listingId);
        if (foundListing==null){
            return new ResponseEntity<Listing>(foundListing, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Listing>(foundListing, HttpStatus.FOUND);
    }

    @GetMapping("/get-all/")
    public ResponseEntity<List<Listing>> getAll(Model model, @RequestHeader("Authorization") String token) {
        List<Listing> foundListing = listingService.findAll(token);
        if (foundListing==null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundListing, HttpStatus.FOUND);
    }

    @GetMapping("/get-by-seller")
    public ResponseEntity<List<Listing>> getBySellerId(Model model, @RequestHeader("Authorization") String token) {
        List<Listing> foundListing = listingService.findAllBySellerId(token);
        if (foundListing==null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundListing, HttpStatus.FOUND);
    }

    private ModelAndView getModelAndView(String html) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(html);
        return modelAndView;
    }
}
