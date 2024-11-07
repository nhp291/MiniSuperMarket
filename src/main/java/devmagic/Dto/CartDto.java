package devmagic.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CartDto {
    private List<CartItemDto> items;
    private String paymentMethod;
    private String customerName;
    private String customerAddress;
}
