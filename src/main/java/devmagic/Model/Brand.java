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
@Table(name = "Brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brandId;

    @Column(name = "brand_name", nullable = false)
    @NotEmpty(message = "Tên thương hiệu không được để trống")
    private String brandName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;
}
