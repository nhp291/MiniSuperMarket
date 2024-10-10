package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId; // Khóa chính

    @Column(name = "category_name", nullable = false)
    private String categoryName; // Tên danh mục

    @Column(name = "image_url")
    private String imageUrl; // Đường dẫn hình ảnh

    @Column(name = "description")
    private String description; // Mô tả danh mục
}
