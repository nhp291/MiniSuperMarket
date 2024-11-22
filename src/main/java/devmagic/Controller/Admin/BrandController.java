package devmagic.Controller.Admin;

import devmagic.Model.Brand;
import devmagic.Service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/Brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/BrandList")
    public String getBrandList(Model model) {
        // Fetch the list of brands
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Brand List Page");
        model.addAttribute("viewName", "admin/menu/BrandList");
        return "admin/layout";
    }

    @GetMapping("/AddBrand")
    public String addBrand(Model model) {
        model.addAttribute("pageTitle", "Add Brand Page");
        model.addAttribute("viewName", "admin/menu/AddBrand");
        return "admin/layout";
    }

    @GetMapping("/EditBrand/{id}")
    public String editBrand(@PathVariable Integer id, Model model) {
        Brand brand = brandService.getBrandById(id);
        if (brand == null) {
            return "redirect:/Brand/BrandList";
        }
        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Edit Brand");
        model.addAttribute("viewName", "admin/menu/AddBrand");
        return "admin/layout";
    }

    @PostMapping("/AddBrand")
    public String createBrand(@ModelAttribute Brand brand) {
        brandService.createBrand(brand);
        return "redirect:/Brand/BrandList";
    }

    @PostMapping("/UpdateBrand/{id}")
    public String updateBrand(@PathVariable Integer id, @ModelAttribute Brand brand) {
        brandService.updateBrand(id, brand);
        return "redirect:/Brand/BrandList";
    }

    @GetMapping("/DeleteBrand/{id}")
    public String deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return "redirect:/Brand/BrandList";
    }
}
