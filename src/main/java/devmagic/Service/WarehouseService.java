package devmagic.Service;

import devmagic.Model.Warehouse;
import devmagic.Reponsitory.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {
    @Autowired
    private WarehouseRepository warehouseRepository;

    public Page<Warehouse> getPaginatedWarehouses(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }


    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        System.out.println("Fetched Warehouses: " + warehouses); // In ra log danh sách kho
        return warehouses;
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);  // Save the new warehouse
    }

    public Warehouse updateWarehouse(Integer id, Warehouse warehouse) {
        warehouse.setWarehouseId(id);  // Set the existing warehouse ID
        return warehouseRepository.save(warehouse);  // Update the warehouse
    }

    public void deleteWarehouse(Integer id) {
        warehouseRepository.deleteById(id);  // Delete the warehouse by ID
    }

    public Page<Warehouse> getFilteredWarehouses(String warehouseName, Pageable pageable) {
        return warehouseRepository.findByWarehouseNameContainingIgnoreCase(warehouseName, pageable);
    }

    public Warehouse findById(Integer id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + id));
    }


}
