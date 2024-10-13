package devmagic.Controller;

import devmagic.Model.Product;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("sale") double sale,
            @RequestParam("brandId") int brandId,
            @RequestParam("categoryId") int categoryId,
            @RequestParam("warehouseId") int warehouseId,
            @RequestParam("image") MultipartFile image) throws Exception {


        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setSale(sale);
        product.setBrand(productService.getBrandById(brandId)); // Phải triển khai phương thức này
        product.setCategory(productService.getCategoryById(categoryId)); // Phải triển khai phương thức này
        product.setWarehouse(productService.getWarehouseById(warehouseId)); // Phải triển khai phương thức này
//        product.setImageUrl(imageUrl);

        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable int id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("sale") double sale,
            @RequestParam(value = "brandId", required = false) Integer brandId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "warehouseId", required = false) Integer warehouseId,
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {

        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setSale(sale);
        if (brandId != null) product.setBrand(productService.getBrandById(brandId));
        if (categoryId != null) product.setCategory(productService.getCategoryById(categoryId));
        if (warehouseId != null) product.setWarehouse(productService.getWarehouseById(warehouseId));
//        if (image != null) {
//            String imageUrl = firebaseService.uploadFile(image);
//            product.setImageUrl(imageUrl);
//        }

        productService.addProduct(product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
