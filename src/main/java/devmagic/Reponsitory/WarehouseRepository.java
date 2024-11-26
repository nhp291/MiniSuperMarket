package devmagic.Reponsitory;

import devmagic.Model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    Page<Warehouse> findByWarehouseNameContainingIgnoreCase(String warehouseName, Pageable pageable);

}
