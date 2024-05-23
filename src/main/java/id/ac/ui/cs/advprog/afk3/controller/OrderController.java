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
import java.util.Map;

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

    @PostMapping(value="/create", consumes = {"application/json"})
    public ResponseEntity<Order> createOrderPagePost(@RequestBody Map<String,Integer> order, @RequestHeader("Authorization") String token){
        System.out.println(order);
        Order order1 = orderService.createOrder(order, token);

        return new ResponseEntity<>(order1, HttpStatus.CREATED);
    }

    @PostMapping("/to-seller")
    public ResponseEntity<List> postOrderToSeller(Model model,@RequestHeader("Authorization") String token){
        List<Order> orderList = orderService.findAllWithSeller(token);
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @PostMapping("/set-status/{id}")
    public ResponseEntity<Order> postSetStatusById(@PathVariable("id")String orderId, @ModelAttribute("status") String status,@RequestHeader("Authorization") String token){
        System.out.println("/order/set-status"+orderId+" "+status);
        Order order = orderService.updateStatus(orderId, status, token);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    private ModelAndView getModelAndView(String html) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(html);
        return modelAndView;
    }
}
