package devmagic.Controller.Admin;

import devmagic.Model.Product;
import devmagic.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/ProductList")
    public String ProductList(Model model) {
        model.addAttribute("pageTitle", "Product List Page");
        model.addAttribute("viewName", "admin/menu/ProductList");
        return "admin/layout";
    }

    @GetMapping("/AddProduct")
    public String AddProduct(Model model) {
        model.addAttribute("pageTitle", "Add Product Page");
        model.addAttribute("viewName", "admin/menu/AddProduct");
        return "admin/layout";
    }

    @GetMapping("/ProductDetail")
    public String ProductDetail(Model model) {
        model.addAttribute("pageTitle", " Product Detail Page");
        model.addAttribute("viewName", "admin/menu/ProductDetail");
        return "admin/layout";
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
