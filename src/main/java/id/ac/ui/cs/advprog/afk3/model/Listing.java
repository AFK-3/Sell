package id.ac.ui.cs.advprog.afk3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.*;

@Getter @Setter
@Entity
@NoArgsConstructor
@Table(name = "Listing")
@SQLDelete(sql = "UPDATE listing SET deleted = true WHERE id=?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedProductFilter", condition = "(:isDeleted IS NULL OR deleted = :isDeleted)")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String sellerUsername;
    private String name;
    private String description;
    private int quantity;

    @ColumnDefault("false")
    private boolean deleted = Boolean.FALSE;

    @ColumnDefault("10000")
    private int price;

    @JsonIgnore
    @ManyToMany(mappedBy = "listings")
    private List<Order> orders = new ArrayList<>();

}
