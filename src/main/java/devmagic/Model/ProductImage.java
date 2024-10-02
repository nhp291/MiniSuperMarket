package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ProductImage")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId; // Khóa chính với tự động tăng

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Khóa ngoại tham chiếu đến bảng Product

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // Đường dẫn đến hình ảnh sản phẩm

    // Getters and Setters
    // ...
}
