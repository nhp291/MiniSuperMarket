package devmagic.Controller.Admin;

import devmagic.Model.Warehouse;
import devmagic.Service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("Warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    // GET list warehouses
    @GetMapping("/WarehouseList")
    public String getFilteredWarehouses(@RequestParam(defaultValue = "") String warehouseName,
                                        @RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Warehouse> warehouses = warehouseService.getFilteredWarehouses(warehouseName, pageable);
        model.addAttribute("warehouses", warehouses);
        model.addAttribute("pageTitle", "Filtered Warehouses List");
        model.addAttribute("viewName", "admin/menu/WareHouse");
        model.addAttribute("totalPages", warehouses.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/layout";
    }

    // GET for adding new warehouse
    @GetMapping("/new")
    public String showCreateWarehouseForm(Model model) {
        Warehouse warehouse = new Warehouse();
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("pageTitle", "Add New Warehouse");
        return "admin/warehouse/create";  // Create warehouse view (you need this view)
    }

    // POST to create a new warehouse
    @PostMapping("/create")
    public String createWarehouse(@ModelAttribute Warehouse warehouse, Model model) {
        warehouseService.createWarehouse(warehouse);
        return "redirect:/Warehouses/";  // Redirect after creation
    }

    // GET for editing an existing warehouse
    @GetMapping("/{id}/edit")
    public String showEditWarehouseForm(@PathVariable("id") Integer id, Model model) {
        Warehouse warehouse = warehouseService.getAllWarehouses().stream()
                .filter(w -> w.getWarehouseId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("pageTitle", "Edit Warehouse");
        return "admin/warehouse/edit";  // Edit warehouse view (you need this view)
    }

    // PUT to update warehouse
    @PutMapping("/{id}")
    public String updateWarehouse(@PathVariable("id") Integer id, @ModelAttribute Warehouse warehouse) {
        warehouseService.updateWarehouse(id, warehouse);
        return "redirect:/Warehouses/";  // Redirect after updating
    }

    // DELETE to remove warehouse
    @DeleteMapping("/{id}")
    public String deleteWarehouse(@PathVariable("id") Integer id) {
        warehouseService.deleteWarehouse(id);
        return "redirect:/Warehouses/";  // Redirect after deletion
    }

}
