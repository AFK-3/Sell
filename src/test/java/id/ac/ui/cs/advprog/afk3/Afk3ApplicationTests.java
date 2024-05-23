package id.ac.ui.cs.advprog.afk3;

import id.ac.ui.cs.advprog.afk3.controller.ListingController;
import id.ac.ui.cs.advprog.afk3.controller.OrderController;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.Executor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootTest
@ActiveProfiles("test")
class Afk3ApplicationTests {

    @Resource(name="listingController")
    private ListingController listingController;
    @Resource(name="orderController")
    private OrderController orderController;


    @Test
    void contextLoads() {
        Afk3Application asyncConfig = new Afk3Application();
        assertThat(listingController).isNotNull();
        assertThat(orderController).isNotNull();
        Executor executor = asyncConfig.taskExecutor();
        assertNotNull(executor);

        // Verify the executor properties
        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(2, threadPoolTaskExecutor.getCorePoolSize());
        assertEquals(2, threadPoolTaskExecutor.getMaxPoolSize());
        assertEquals(500, threadPoolTaskExecutor.getQueueCapacity());
        assertEquals("thread", threadPoolTaskExecutor.getThreadNamePrefix());
    }

}
