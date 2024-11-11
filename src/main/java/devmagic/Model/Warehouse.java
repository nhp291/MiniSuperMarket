package devmagic.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int warehouseId;

    @Column(name = "warehouse_name", nullable = false)
    @NotEmpty(message = "Tên kho không được để trống")
    private String warehouseName;

    @Column(name = "location", nullable = false)
    @NotEmpty(message = "Địa điểm kho không được để trống")
    private String location;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;


}
