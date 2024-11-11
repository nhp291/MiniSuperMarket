package devmagic.Controller.Admin;

import devmagic.Dto.ProductDTO;
import devmagic.Model.Product;
import devmagic.Model.ProductImage;
import devmagic.Reponsitory.*;
import devmagic.Service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductService productService;

    @GetMapping("/ProductList")
    public String viewProductList(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Product List");
        model.addAttribute("viewName", "admin/menu/ProductList");
        return "admin/layout";
    }

    @GetMapping("/AddProduct")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("pageTitle", "Add Product");
        model.addAttribute("viewName", "admin/menu/AddProduct");
        return "admin/layout";
    }

    @PostMapping("/AddProduct")
    public String addProduct(@ModelAttribute @Valid ProductDTO productDTO,
                             @RequestParam("images") List<MultipartFile> files,
                             Model model) throws IOException {

        if (productDTO.getBrandId() == null || !brandRepository.existsById(productDTO.getBrandId())) {
            model.addAttribute("error", "Thương hiệu không hợp lệ");
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "admin/menu/AddProduct"; // Trở lại form nếu có lỗi
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(new BigDecimal(productDTO.getPrice()));
        product.setSale(new BigDecimal(productDTO.getSale()));
        product.setStockQuantity(productDTO.getStockQuantity());

        product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(null));
        product.setWarehouse(warehouseRepository.findById(productDTO.getWarehouseId()).orElse(null));
        product.setBrand(brandRepository.findById(productDTO.getBrandId()).orElse(null));

        Product savedProduct = productService.createProduct(product, files);

        return "redirect:/Products/ProductList";
    }

    @GetMapping("/EditProduct/{id}")
    public String editProductForm(@PathVariable("id") int id, Model model) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(product.getName());
            productDTO.setDescription(product.getDescription());
            productDTO.setPrice(product.getPrice().doubleValue()); // Chuyển giá trị BigDecimal sang double
            productDTO.setSale(product.getSale() != null ? product.getSale().doubleValue() : 0); // Chuyển sale nếu có
            productDTO.setStockQuantity(product.getStockQuantity());
            productDTO.setCategoryId(product.getCategory().getCategoryId());
            productDTO.setWarehouseId(product.getWarehouse().getWarehouseId());
            productDTO.setBrandId(product.getBrand().getBrandId());

            model.addAttribute("productDTO", productDTO);
            model.addAttribute("images", product.getImages()); // Truyền danh sách ảnh để hiển thị
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("pageTitle", "Edit Product");
            model.addAttribute("viewName", "admin/menu/AddProduct");
            return "admin/layout";
        }
        model.addAttribute("error", "Sản phẩm không tồn tại");
        return "redirect:/Products/ProductList";
    }

    @PostMapping("/EditProduct/{id}")
    public String editProduct(@PathVariable("id") int id, @ModelAttribute @Valid ProductDTO productDTO,
                              @RequestParam("imageFiles") List<MultipartFile> imageFiles,
                              Model model) throws IOException {
        if (productDTO.getBrandId() == null || !brandRepository.existsById(productDTO.getBrandId())) {
            model.addAttribute("error", "Thương hiệu không hợp lệ");
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "admin/menu/AddProduct";
        }

        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();

            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(null));
            existingProduct.setPrice(new BigDecimal(productDTO.getPrice()));
            existingProduct.setSale(new BigDecimal(productDTO.getSale()));
            existingProduct.setStockQuantity(productDTO.getStockQuantity());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setWarehouse(warehouseRepository.findById(productDTO.getWarehouseId()).orElse(null));
            existingProduct.setBrand(brandRepository.findById(productDTO.getBrandId()).orElse(null));

            productRepository.save(existingProduct);

            productImageRepository.deleteAllByProduct(existingProduct);

            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String fileName = productService.saveImage(file); // Lưu file và lấy tên đường dẫn
                    ProductImage image = new ProductImage();
                    image.setImageUrl(fileName); // Sử dụng tên file đã lưu
                    image.setProduct(existingProduct);
                    productImageRepository.save(image);
                }
            }

            return "redirect:/Products/ProductList";
        }

        model.addAttribute("error", "Sản phẩm không tồn tại");
        return "redirect:/Products/ProductList"; // Nếu không tìm thấy sản phẩm, quay lại danh sách
    }

    @Transactional // Áp dụng @Transactional ở đây nếu cả `deleteAllByProduct` và `delete` đều được gọi
    @GetMapping("/DeleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            productImageRepository.deleteAllByProduct(product); // Đảm bảo phương thức này là transactional
            productRepository.delete(product); // Đảm bảo phương thức này là transactional
        }
        return "redirect:/Products/ProductList";
    }

}
