package id.ac.ui.cs.advprog.afk3.controller;

import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import id.ac.ui.cs.advprog.afk3.service.ListingService;
import id.ac.ui.cs.advprog.afk3.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ListingService listingService;

    private String createHtml = "orderCreate";

    @GetMapping("/create")
    public ModelAndView createOrderPage(Model model){
        List<Listing> listingList = listingService.findAll();
        Order dto = new Order();
        dto.setListings(new ArrayList<Listing>());
        for (Listing p : listingList){
            dto.getListings().add(p);
            System.out.println("order/create "+p.getSellerUsername());
        }
        ModelAndView modelAndView = getModelAndView(createHtml);
        modelAndView.addObject("form", dto);
        System.out.println("zczc"+listingList);
        return modelAndView;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrderPagePost(@RequestBody Order order, @RequestHeader("Authorization") String token){
        System.out.println(order);
        for (Listing listing : order.getListings()){
            Listing temp = listingService.findById(listing.getId());
            listing.setSellerUsername(temp.getSellerUsername());
        }
        Order order1 = orderService.createOrder(order, token);

        return new ResponseEntity<>(order1, HttpStatus.CREATED);
    }

    @GetMapping("/to-seller")
    public String getOrderToSeller(Model model){
        model.addAttribute("owner", new Order());
        return "orderGetToSeller";
    }

    @PostMapping("/to-seller")
    public ResponseEntity<List> postOrderToSeller(@ModelAttribute("owner") Order order, Model model){
        String authorUsername = order.getAuthorUsername();
        System.out.println("zczcz"+orderService.findAllWithSeller(authorUsername));
        List<Order> orderList = orderService.findAllWithSeller(authorUsername);
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }
    @PostMapping("/set-status/{id}")
    public ResponseEntity<Order> postSetStatusById(@PathVariable("id")String orderId, @ModelAttribute("status") String status,@RequestHeader("Authorization") String token){
        System.out.println("/order/set-status"+orderId+" "+status);
        Order order = orderService.updateStatus(orderId, status, token);
        System.out.println(orderService.findById(orderId).getStatus());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    private ModelAndView getModelAndView(String html) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(html);
        return modelAndView;
    }
}
