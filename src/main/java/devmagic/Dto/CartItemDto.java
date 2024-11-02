package devmagic.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDto {
    private Integer productId;
    private String productName;
    private String productImage;
    private int quantity;
    private double price;
}
