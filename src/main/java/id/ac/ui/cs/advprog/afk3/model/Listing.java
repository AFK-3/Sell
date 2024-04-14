package id.ac.ui.cs.advprog.afk3.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Listing {
    private UUID id;

    private String sellerUsername;
    private String name;
    private String description;
    private int quantity;
}
