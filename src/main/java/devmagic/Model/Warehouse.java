package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int warehouseId; // Khóa chính

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName; // Tên kho

    @Column(name = "location", nullable = false)
    private String location; // Địa điểm kho

}
