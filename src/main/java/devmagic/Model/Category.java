package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId; // Khóa chính với tự động tăng

    @Column(name = "category_name", nullable = false)
    private String categoryName; // Tên danh mục

    @Column(name = "image_url")
    private String imageUrl; // Đường dẫn hình ảnh

    @Column(name = "description")
    private String description; // Mô tả danh mục

}
