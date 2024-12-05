package devmagic.Dto;

import devmagic.Model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private String imageUrl;
    private String productName;
    private int quantity;
    private BigDecimal price; // Thay đổi kiểu từ double sang BigDecimal
    private Product product; // Thuộc tính Product

    public Product getProduct() {
        return product;
    }
}
