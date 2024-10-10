package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId; // Khóa chính

    @Column(name = "name", nullable = false)
    private String name; // Tên sản phẩm

    @Column(name = "description")
    private String description; // Mô tả sản phẩm

    @Column(name = "price", nullable = false)
    private double price; // Giá sản phẩm

    @Column(name = "sale")
    private double sale; // Giá khuyến mãi

    @Column(name = "stock_quantity", nullable = false)
    private int Quantity; // Số lượng tồn kho

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Danh mục sản phẩm

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand; // Thương hiệu sản phẩm

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // Kho hàng

    // Liên kết một sản phẩm với nhiều ảnh
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

}