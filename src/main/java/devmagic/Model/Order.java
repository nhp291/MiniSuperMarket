package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId; // Khóa chính

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Tài khoản

    @Column(name = "order_date", nullable = false)
    private Date orderDate; // Ngày đặt hàng

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // Trạng thái thanh toán

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Phương thức thanh toán

}

