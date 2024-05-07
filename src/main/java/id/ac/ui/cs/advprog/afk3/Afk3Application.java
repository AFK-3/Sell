package id.ac.ui.cs.advprog.afk3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Afk3Application {

    public static void main(String[] args) {
        SpringApplication.run(Afk3Application.class, args);
    }

}
