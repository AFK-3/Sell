package id.ac.ui.cs.advprog.afk3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.ColumnDefault;

@Getter @Setter
@Entity
@NoArgsConstructor
@Table(name = "Listing")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String sellerUsername;
    private String name;
    private String description;
    private int quantity;

    @ColumnDefault("10000")
    private int price;

    @JsonIgnore
    @ManyToMany(mappedBy = "listings")
    private List<Order> orders = new ArrayList<>();

    @PreRemove
    public void removeOrderAssociations() {
        for (Order order: this.orders) {
            order.getListings().remove(this);
        }
    }
}
