package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int warehouseId; // Khóa chính với tự động tăng

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName; // Tên kho

    @Column(name = "location", nullable = false)
    private String location; // Vị trí kho

}
