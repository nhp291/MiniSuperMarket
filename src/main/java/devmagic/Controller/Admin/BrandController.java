package devmagic.Controller.Admin;

import devmagic.Model.Brand;
import devmagic.Service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    @Autowired
    private BrandService brandsService;

    @GetMapping("/BrandList")
    public String BrandList(Model model) {
        model.addAttribute("pageTitle", "Brand List Page");
        model.addAttribute("viewName", "admin/menu/BrandList");
        return "admin/layout";
    }

    @GetMapping("/AddBrand")
    public String AddBrand(Model model) {
        model.addAttribute("pageTitle", "Add Brand Page");
        model.addAttribute("viewName", "admin/menu/AddBrand");
        return "admin/layout";
    }

    @GetMapping
    public List<Brand> getAllBrands() {
        return brandsService.getAllBrands();
    }

    @PostMapping
    public Brand createBrand(@RequestBody Brand brand) {
        return brandsService.createBrand(brand);
    }

    @PutMapping("/{id}")
    public Brand updateBrand(@PathVariable Integer id, @RequestBody Brand brand) {
        return brandsService.updateBrand(id, brand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        brandsService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
