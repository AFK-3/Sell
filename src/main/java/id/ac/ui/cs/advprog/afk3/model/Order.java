package id.ac.ui.cs.advprog.afk3.model;

import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter @Setter
@Entity
@NoArgsConstructor
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Long orderTime;
    private String authorUsername;
    private String status;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name="order_has_listings",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "id")
    )
    private List<Listing> listings = new ArrayList<>();

    @ElementCollection
    private Map<String, Integer> listingQuantity;
}
