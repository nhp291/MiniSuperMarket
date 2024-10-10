package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brandId; // Khóa chính

    @Column(name = "brand_name", nullable = false)
    private String brandName; // Tên thương hiệu

    @Column(name = "image_url")
    private String imageUrl; // Đường dẫn hình ảnh

    @Column(name = "description")
    private String description; // Mô tả thương hiệu

}
