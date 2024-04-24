package id.ac.ui.cs.advprog.afk3.controller;

import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.service.ListingService;
import id.ac.ui.cs.advprog.afk3.service.OrderService;
import id.ac.ui.cs.advprog.afk3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ListingService listingService;
    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String createOrderPage(Model model){
        List<Listing> listingList = listingService.findAll();
        Order dto = new Order();
        dto.setListings(new ArrayList<Listing>());
        for (Listing p : listingList){
            dto.getListings().add(p);
            System.out.println("order/create "+p.getSellerUsername());
        }
        model.addAttribute("form", dto);
        System.out.println("zczc"+listingList);
        return "orderCreate";
    }

    @PostMapping("/create")
    public String createOrderPagePost(@ModelAttribute("form") Order order, @RequestHeader("Authorization") String token){
        List<Listing> listings = new ArrayList<>();
        for (Listing listing : order.getListings()){
            Listing temp = listingService.findById(listing.getId().toString());
            listing.setSellerUsername(temp.getSellerUsername());
            listings.add(listing);
            System.out.println(listing.getId()+" zczc "+listing.getName()+" "+listing.getQuantity()+" "+listing.getSellerUsername());
        }
        orderService.createOrder(order, token);

        System.out.println("/order/create"+order.getId());

        return "redirect:../listing/list";
    }

    @GetMapping("/to-seller")
    public String getOrderToSeller(Model model){
        model.addAttribute("owner", new Order());
        return "orderGetToSeller";
    }

    @PostMapping("/to-seller")
    public String postOrderToSeller(@ModelAttribute("owner") Order order, Model model){
        String authorUsername = order.getAuthorUsername();
        System.out.println("zczcz"+orderService.findAllWithSeller(authorUsername));
        model.addAttribute("orders", orderService.findAllWithSeller(authorUsername));
        return "orderPostToSeller";
    }
    @PostMapping("/set-status/{id}")
    public String postSetStatusById(@PathVariable("id")String orderId, @ModelAttribute("status") String status,@RequestHeader("Authorization") String token){
        System.out.println("/order/set-status"+orderId+" "+status);
        orderService.updateStatus(orderId, status, token);
        System.out.println(orderService.findById(orderId).getStatus());
        return "redirect:../to-seller";
    }
}
