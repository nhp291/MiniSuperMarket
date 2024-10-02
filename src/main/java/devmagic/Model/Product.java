package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId; // Khóa chính với tự động tăng

    @Column(name = "name", nullable = false)
    private String name; // Tên sản phẩm

    @Column(name = "description")
    private String description; // Mô tả sản phẩm

    @Column(name = "price", nullable = false)
    private double price; // Giá sản phẩm

    @Column(name = "sale")
    private double sale; // Giá khuyến mãi

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Khóa ngoại tham chiếu đến bảng Categories

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand; // Khóa ngoại tham chiếu đến bảng Brands

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // Khóa ngoại tham chiếu đến bảng Warehouse

    @Column(name = "image_url")
    private String imageUrl; // URL của hình ảnh sản phẩm

    // Các phương thức getter và setter tự động được tạo ra bởi Lombok thông qua @Data
}
