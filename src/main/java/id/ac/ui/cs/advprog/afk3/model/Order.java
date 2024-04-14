package id.ac.ui.cs.advprog.afk3.model;

import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Order {
    private UUID id;
    private List<Listing> listings;
    private Long orderTime;
    private String authorUsername;
    private String status;
}
