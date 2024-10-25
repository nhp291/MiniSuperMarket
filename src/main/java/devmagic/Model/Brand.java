package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brandId; // Khóa chính với tự động tăng

    @Column(name = "brand_name", nullable = false)
    private String brandName; // Tên thương hiệu

    @Column(name = "image_url")
    private String imageUrl; // Đường dẫn hình ảnh

    @Column(name = "description")
    private String description; // Mô tả thương hiệu
}

