package id.ac.ui.cs.advprog.afk3.controller;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/listing")
public class ListingController {
    String listHTML = "listingList";
    String createHTML = "listingCreate";
    String editHTML = "listingEdit";
    @Autowired
    private ListingService listingService;

    @GetMapping("/create")
    public String createListingPage(Model model){
        Listing listing = new Listing();
        model.addAttribute("listing", listing);
        return createHTML;
    }

    @PostMapping("/create")
    public String createListingPost(@ModelAttribute("product") Listing listing, Model model){
        listingService.create(listing);
        return "redirect:list";
    }

    @GetMapping("/list")
    public String listingListPage(Model model){
        List<Listing> allListings = listingService.findAll();
        model.addAttribute("listings", allListings);
        return listHTML;
    }

    @GetMapping(value="/edit/{listingId}")
    public String editProductPage(Model model, @PathVariable("listingId") String productId){
        Listing listing = listingService.findById(productId);
        if (listing!=null){
            model.addAttribute("listing", listing);
            return editHTML;
        }
        return "redirect:../list";
    }

    @PostMapping("/edit")
    public String editProductPost(@ModelAttribute("listing") Listing listing, Model model){
        listingService.update(listing.getId().toString(), listing);
        return "redirect:list";
    }

    @PostMapping("/delete")
    public String deleteListing(Model model, @RequestParam("listingId") String listingId){
        listingService.deleteListingById(listingId);
        return "redirect:list";
    }
}
