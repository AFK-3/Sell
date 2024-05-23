package id.ac.ui.cs.advprog.afk3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.afk3.model.*;
import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.service.ListingServiceImpl;
import id.ac.ui.cs.advprog.afk3.service.OrderServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import id.ac.ui.cs.advprog.afk3.service.ListingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = OrderController.class)
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ListingServiceImpl listingService ;
    @MockBean
    private OrderServiceImpl orderService;

    @Autowired
    private ObjectMapper objectMapper;
    private String token = "a";
    private List<Order> orders = new ArrayList<>();

    @Test
    public void testGetCreatePage() throws Exception{
        List<Listing> listingList = new ArrayList<>();
        Listing listing = new Listing();
        listing.setName("a");
        listingList.add(listing);
        when(listingService.findAll()).thenReturn(listingList);
        mvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Order")));
    }

    @Test
    public void testPostCreatePage() throws Exception{
        Order dto = new Order();
        List<Listing> listingList = new ArrayList<>();
        Listing listing = new Listing();
        listing.setName("a");
        listingList.add(listing);
        dto.setListings(listingList);

        Map<String, Integer> map = new HashMap<>();
        map.put("a",1);

        when(listingService.findById(listing.getId())).thenReturn(listing);
        mvc.perform(post("/order/create").content(asJsonString(map)).contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isCreated());
    }


    @Test
    public void testPostToSeller() throws Exception{
        orders.add(new Order());
        orders.add(new Order());
        when(orderService.findAllWithSeller(token)).thenReturn(orders);
        mvc.perform(post("/order/to-seller").header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void testSetStatus() throws Exception{
        Order order = new Order();
        when(orderService.updateStatus("1",OrderStatus.CANCELLED.name(), token)).thenReturn(order);
        mvc.perform(post("/order/set-status/1").header("Authorization", token).flashAttr("status", OrderStatus.CANCELLED.name()))
                .andExpect(status().isOk());
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
