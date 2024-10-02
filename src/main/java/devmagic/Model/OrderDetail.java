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
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderDetailId; // Khóa chính với tự động tăng

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Khóa ngoại tham chiếu đến bảng Orders

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Khóa ngoại tham chiếu đến bảng Product

    @Column(name = "quantity", nullable = false)
    private int quantity; // Số lượng sản phẩm

    @Column(name = "price", nullable = false)
    private double price; // Giá của sản phẩm tại thời điểm đặt hàng

    // Getters and Setters
    // ...
}

