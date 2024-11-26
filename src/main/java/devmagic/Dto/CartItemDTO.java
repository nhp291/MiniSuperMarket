package devmagic.Dto;

import devmagic.Model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private String imageUrl;
    private String productName;
    private int quantity;
    private double price;
    private Product product; // Thêm thuộc tính Product

    public Product getProduct() {
        return product;
    }
}
