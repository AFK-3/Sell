package id.ac.ui.cs.advprog.afk3.controller;

import id.ac.ui.cs.advprog.afk3.controller.ListingController;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.service.ListingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.management.openmbean.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ListingController.class)
@ActiveProfiles("test")
public class ListingControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ListingServiceImpl listingService;

    @InjectMocks
    private ListingController controller;

    private String token ="a";


    private List<Listing> allListings;

    private  Listing mockAddListingToRepository(Listing listing){
        allListings.add(listing);
        return listing;
    }

    private boolean mockEditListingToRepository(Listing listing){
        for(Listing datum : allListings){
            if(datum.getId().equals(listing.getId())){
                datum.setName(listing.getName());
                datum.setQuantity(listing.getQuantity());
                return true;
            }
        }
        return false;
    }

    Listing createAndSaveListing(){
        Listing listing = new Listing();
        listing.setId("1");
        listing.setName("bambang");
        allListings.add(listing);
        return listing;
    }

    @BeforeEach
    void setUp(){
        allListings = new ArrayList<>();
    }

    @AfterEach
    void deleteRepository(){
        allListings = null;
    }

    @Test
    public void createPageTest() throws Exception{
        mvc.perform(get("/listing/create"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create New Listing")));
    }
    @Test
    public void listPageTest() throws Exception{
        mvc.perform(get("/listing/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Listing's List")));
    }
    @Test
    public void createListingPostTest() throws Exception{
        Listing listing = createAndSaveListing();

        when(listingService.create(listing, token)).thenReturn(mockAddListingToRepository(listing));
        mvc.perform(post("/listing/create").flashAttr("product",listing).header("Authorization", token))
                .andExpect(status().isOk());

        when(listingService.findAll()).thenReturn(allListings);
        mvc.perform(get("/listing/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Listing's List")))
                .andExpect(content().string(containsString("bambang")));
    }

    @Test
    public void editListingPostTest() throws Exception{

        Listing listing = createAndSaveListing();

        when(listingService.create(listing, token)).thenReturn(mockAddListingToRepository(listing));
        mvc.perform(post("/listing/create").flashAttr("listing",listing)
                        .header("Authorization", token))
                        .andExpect(status().isOk());

        Listing listing2 = new Listing();
        listing2.setId(listing.getId());
        listing2.setName("adi");
        mvc.perform(post("/listing/edit").flashAttr("listing",listing2)
                        .header("Authorization", token))
                        .andExpect(status().isOk());

        mockEditListingToRepository(listing2); // assume listingService.update is functionally good

        when(listingService.findAll()).thenReturn(allListings);
        mvc.perform(get("/listing/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Listing's List")))
                .andExpect(content().string(containsString("adi")));
    }

    @Test
    public void deleteListingPostTest() throws Exception{

        Listing listing = createAndSaveListing();

        when(listingService.create(listing, token)).thenReturn(mockAddListingToRepository(listing));
        mvc.perform(post("/listing/create").flashAttr("listing",listing)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        when(listingService.findAll()).thenReturn(allListings);
        mvc.perform(get("/listing/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Listing's List")))
                .andExpect(content().string(containsString("bambang")));

        when(listingService.deleteListingById("1", token)).thenReturn(true);
        mvc.perform(post("/listing/delete").header("Authorization", token).param("listingId",listing.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteListingPostTestNotFound() throws Exception{

        Listing listing = createAndSaveListing();

        when(listingService.create(listing, token)).thenReturn(mockAddListingToRepository(listing));
        mvc.perform(post("/listing/create").flashAttr("listing",listing)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        when(listingService.findAll()).thenReturn(allListings);
        mvc.perform(get("/listing/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Listing's List")))
                .andExpect(content().string(containsString("bambang")));

        when(listingService.deleteListingById("1", token)).thenReturn(false);
        mvc.perform(post("/listing/delete").header("Authorization", token).param("listingId",listing.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetByIdListing() throws Exception{

        Listing listing = createAndSaveListing();

        when(listingService.findById(listing.getId())).thenReturn(listing);
        MvcResult result = mvc.perform(get("/listing/get-by-id/1")
                        .header("Authorization", token))
                .andExpect(status().isFound()).andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("id"));
        assertTrue(content.contains("name"));
        assertTrue(content.contains("quantity"));
    }

    @Test
    public void testGetByIdListingNotFound() throws Exception{
        when(listingService.findById("2")).thenReturn(null);
        MvcResult result = mvc.perform(get("/listing/get-by-id/2")
                        .header("Authorization", token))
                .andExpect(status().isNotFound()).andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(content.contains("id"));
        assertFalse(content.contains("name"));
        assertFalse(content.contains("quantity"));
    }

    @Test
    public void testGetBySeller() throws Exception{
        Listing listing = createAndSaveListing();
        when(listingService.findAllBySellerId(token)).thenReturn(allListings);
        MvcResult result = mvc.perform(get("/listing/get-by-seller/")
                        .header("Authorization", token))
                .andExpect(status().isFound()).andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println(content);
        assertTrue(content.contains("id"));
        assertTrue(content.contains("name"));
        assertTrue(content.contains("quantity"));
    }
    @Test
    public void testGetBySellerNotValid() throws Exception{
        Listing listing = createAndSaveListing();
        when(listingService.findAllBySellerId(token)).thenReturn(null);
        MvcResult result = mvc.perform(get("/listing/get-by-seller/")
                        .header("Authorization", token))
                .andExpect(status().isNotFound()).andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(content.contains("id"));
        assertFalse(content.contains("name"));
        assertFalse(content.contains("quantity"));
    }
}
