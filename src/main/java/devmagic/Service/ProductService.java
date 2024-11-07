package devmagic.Service;

import devmagic.Model.Product;
import devmagic.Model.ProductImage;
import devmagic.Reponsitory.ProductImageRepository;
import devmagic.Reponsitory.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private final String UPLOAD_DIRECTORY = "./uploads/products/";

    /**
     * Lấy tất cả sản phẩm
     * @return Danh sách tất cả sản phẩm
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Lấy sản phẩm theo ID
     * @param id ID của sản phẩm cần tìm
     * @return Sản phẩm nếu tìm thấy, null nếu không tìm thấy
     */
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Tạo sản phẩm mới
     * @param product Đối tượng Product cần tạo
     * @param file File hình ảnh của sản phẩm
     * @return Sản phẩm đã được tạo
     */
    @Transactional
    public Product createProduct(Product product, MultipartFile file) {
        Product savedProduct = productRepository.save(product);
        if (file != null && !file.isEmpty()) {
            String fileName = saveImage(file);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setImageUrl(fileName);
            productImageRepository.save(productImage);
        }
        return savedProduct;
    }

    /**
     * Cập nhật sản phẩm
     * @param id ID của sản phẩm cần cập nhật
     * @param product Thông tin sản phẩm mới
     * @param file File hình ảnh mới (nếu có)
     * @return Sản phẩm đã được cập nhật, null nếu không tìm thấy sản phẩm
     */
    @Transactional
    public Product updateProduct(Integer id, Product product, MultipartFile file) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setSale(product.getSale());
            existingProduct.setStockQuantity(product.getStockQuantity());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setBrand(product.getBrand());
            existingProduct.setWarehouse(product.getWarehouse());

            Product updatedProduct = productRepository.save(existingProduct);

            if (file != null && !file.isEmpty()) {
                String fileName = saveImage(file);
                ProductImage productImage = new ProductImage();
                productImage.setProduct(updatedProduct);
                productImage.setImageUrl(fileName);
                productImageRepository.save(productImage);
            }

            return updatedProduct;
        }
        return null;
    }

    /**
     * Xóa sản phẩm theo ID
     * @param id ID của sản phẩm cần xóa
     */
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    /**
     * Lấy danh sách hình ảnh của sản phẩm
     * @param productId ID của sản phẩm
     * @return Danh sách hình ảnh của sản phẩm
     */
    public List<ProductImage> getImagesByProductId(int productId) {
        return productImageRepository.findByProduct_ProductId(productId);
    }

    /**
     * Lưu file hình ảnh
     * @param file File hình ảnh cần lưu
     * @return Tên file đã lưu
     */
    private String saveImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIRECTORY + fileName);
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}