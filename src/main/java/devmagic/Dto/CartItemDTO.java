package devmagic.Dto;

import devmagic.Model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemDTO {
    private String imageUrl;
    private String productName;
    private int quantity;
    private BigDecimal price; // Giá cuối cùng (dùng giá sale nếu có)
    private BigDecimal totalPrice; // Tổng tiền (giá * số lượng)
    private Product product;

    public CartItemDTO(String imageUrl, String productName, int quantity, BigDecimal price, Product product) {
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.quantity = quantity;
        this.product = product;

        // Sử dụng giá sale nếu có, không thì giá gốc
        if (product.getSale() != null && product.getSale().compareTo(BigDecimal.ZERO) > 0) {
            this.price = product.getSale();
        } else {
            this.price = price;
        }

        this.calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
    }
}
