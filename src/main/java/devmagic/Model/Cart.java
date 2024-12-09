package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", precision = 7, scale = 3, nullable = false)
    private BigDecimal price; // Giá của 1 sản phẩm

    @Transient
    private BigDecimal totalPrice; // Tổng giá của sản phẩm trong giỏ (không lưu trong DB)

    @Transient
    private int totalQuantity; // Tổng số lượng sản phẩm trong giỏ (không lưu trong DB)

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateTotalQuantity(int additionalQuantity) {
        this.totalQuantity += additionalQuantity;
    }
}
