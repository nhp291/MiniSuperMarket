package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId; // Khóa chính với tự động tăng

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Khóa ngoại tham chiếu đến bảng Account

    @Column(name = "order_date", nullable = false)
    private java.util.Date orderDate; // Ngày đặt hàng

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // Trạng thái thanh toán

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Phương thức thanh toán

    // Getters and Setters
    // ...
}

