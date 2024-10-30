package devmagic.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
    private int categoryId;

    @Column(name = "category_name", nullable = false)
    @NotEmpty(message = "Tên danh mục không được để trống")
    private String categoryName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;
}
