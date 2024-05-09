package id.ac.ui.cs.advprog.afk3.model.Builder;

import id.ac.ui.cs.advprog.afk3.model.Enum.OrderStatus;
import id.ac.ui.cs.advprog.afk3.model.Listing;
import id.ac.ui.cs.advprog.afk3.model.Order;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Component
public class OrderBuilder {
    private Order currentOrder;

    public OrderBuilder(){
        this.reset();
    }

    public OrderBuilder reset(){
        currentOrder = new Order();
        firstSetUp();
        return this;
    }

    public OrderBuilder addAuthor(String username){
        currentOrder.setAuthorUsername(username);
        return this;
    }

    public OrderBuilder addStatus(String status){
        if (OrderStatus.contains(status)){
            currentOrder.setStatus(status);
            return this;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public OrderBuilder addListings(List<Listing> listings){
        if (!listings.isEmpty()){
            currentOrder.setListings(listings);
            return this;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public OrderBuilder setCurrent(Order newOrder){
        currentOrder = newOrder;
        return this;
    }

    public OrderBuilder firstSetUp(){
        currentOrder.setId(UUID.randomUUID().toString());
        LocalTime now = LocalTime.now();
        Time orderTime = Time.valueOf(now);
        long orderTimeL = orderTime.getTime();
        currentOrder.setOrderTime(orderTimeL);
        currentOrder.setStatus(OrderStatus.WAITINGPAYMENT.name());
        return this;
    }

    public Order build(){
        return currentOrder;
    }
}
