package devmagic.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CartDto {
    private List<CartItemDto> items;       // Danh sách các sản phẩm trong giỏ hàng
    private String paymentMethod;          // Phương thức thanh toán
    private String customerName;           // Tên khách hàng
    private String customerAddress;        // Địa chỉ khách hàng
}
