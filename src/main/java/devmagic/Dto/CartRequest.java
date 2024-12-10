package devmagic.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartRequest {
    private List<CartItem> cartItems;
    private BigDecimal totalPrice;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CartItem {
        private String productId;
        private int quantity;
    }
}
