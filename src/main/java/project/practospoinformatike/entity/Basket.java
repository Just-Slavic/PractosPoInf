package project.practospoinformatike.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "basket")
public class Basket {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;
    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id")
    private List<Catalog> sell;
    @Column(name = "catalog_name")
    private String name;
    @Column(name = "catalog_price")
    private Integer price;

    public Basket(){}

    public Buyer getBuyer() {
        return buyer;
    }
    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }
    public List<Catalog> getSell() {
        return sell;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public void setSell(List<Catalog> sell) {
        this.sell = sell;
    }
}
