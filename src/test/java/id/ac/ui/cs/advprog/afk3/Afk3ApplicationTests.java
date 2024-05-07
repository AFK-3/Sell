package id.ac.ui.cs.advprog.afk3;

import id.ac.ui.cs.advprog.afk3.controller.ListingController;
import id.ac.ui.cs.advprog.afk3.controller.OrderController;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class Afk3ApplicationTests {

    @Resource(name="listingController")
    private ListingController listingController;
    @Resource(name="orderController")
    private OrderController orderController;

    @Test
    void contextLoads() {
        Afk3Application.main(new String[] {});
        assertThat(listingController).isNotNull();
        assertThat(orderController).isNotNull();
    }

}
