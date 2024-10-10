package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrdersDetail")
public class OrdersDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderDetailId; // Khóa chính

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Đơn hàng

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Sản phẩm

    @Column(name = "quantity", nullable = false)
    private int quantity; // Số lượng sản phẩm

    @Column(name = "price", nullable = false)
    private double price; // Giá của sản phẩm tại thời điểm đặt hàng

}
