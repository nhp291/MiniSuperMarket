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
@RequestMapping("/api/products") // Đường dẫn cơ sở cho tất cả các endpoint liên quan đến sản phẩm
public class ProductController {

    @Autowired
    private ProductRepository productRepository; // Repository để truy cập và thao tác với bảng Product


    @Autowired
    private ProductService productService; // Dịch vụ để xử lý các nghiệp vụ liên quan đến sản phẩm

    // Lấy tất cả sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts(); // Gọi phương thức từ ProductService để lấy danh sách sản phẩm
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = productService.getProductById(id); // Lấy sản phẩm theo ID
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build(); // Trả về sản phẩm hoặc 404 nếu không tìm thấy
    }

    // Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name, // Tên sản phẩm
            @RequestParam("description") String description, // Mô tả sản phẩm
            @RequestParam("price") double price, // Giá sản phẩm
            @RequestParam("sale") double sale, // Giá khuyến mãi
            @RequestParam("brandId") int brandId, // ID của brand
            @RequestParam("categoryId") int categoryId, // ID của category
            @RequestParam("warehouseId") int warehouseId, // ID của warehouse
            @RequestParam("image") MultipartFile image) throws Exception { // Hình ảnh sản phẩm


        // Tạo đối tượng sản phẩm mới
        Product product = new Product();
        product.setName(name); // Thiết lập tên sản phẩm
        product.setDescription(description); // Thiết lập mô tả sản phẩm
        product.setPrice(price); // Thiết lập giá sản phẩm
        product.setSale(sale); // Thiết lập giá khuyến mãi

        // Lấy brand, category, và warehouse từ ID và thiết lập cho sản phẩm
        product.setBrand(productService.getBrandById(brandId));
        product.setCategory(productService.getCategoryById(categoryId));
        product.setWarehouse(productService.getWarehouseById(warehouseId));

        // Lưu sản phẩm vào cơ sở dữ liệu
        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED); // Trả về sản phẩm đã lưu với mã 201
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable int id, // ID của sản phẩm cần cập nhật
            @RequestParam("name") String name, // Tên sản phẩm mới
            @RequestParam("description") String description, // Mô tả sản phẩm mới
            @RequestParam("price") double price, // Giá sản phẩm mới
            @RequestParam("sale") double sale, // Giá khuyến mãi mới
            @RequestParam(value = "brandId", required = false) Integer brandId, // ID của brand (tùy chọn)
            @RequestParam(value = "categoryId", required = false) Integer categoryId, // ID của category (tùy chọn)
            @RequestParam(value = "warehouseId", required = false) Integer warehouseId, // ID của warehouse (tùy chọn)
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception { // Hình ảnh mới (tùy chọn)

        // Tìm sản phẩm theo ID
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy sản phẩm
        }

        // Cập nhật thông tin sản phẩm
        product.setName(name); // Cập nhật tên sản phẩm
        product.setDescription(description); // Cập nhật mô tả sản phẩm
        product.setPrice(price); // Cập nhật giá sản phẩm
        product.setSale(sale); // Cập nhật giá khuyến mãi

        // Cập nhật brand, category và warehouse nếu có ID mới
        if (brandId != null) {
            product.setBrand(productService.getBrandById(brandId));
        }
        if (categoryId != null) {
            product.setCategory(productService.getCategoryById(categoryId));
        }
        if (warehouseId != null) {
            product.setWarehouse(productService.getWarehouseById(warehouseId));
        }

        // Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
        Product updatedProduct = productService.addProduct(product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK); // Trả về sản phẩm đã cập nhật
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        boolean deleted = productService.deleteProduct(id); // Gọi phương thức xóa sản phẩm
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); // Trả về 204 nếu xóa thành công, 404 nếu không tìm thấy sản phẩm
    }
}
