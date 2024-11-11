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

    private final String UPLOAD_DIRECTORY = "./Image/imageUrl/"; // Thư mục lưu trữ ảnh

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product createProduct(Product product, List<MultipartFile> files) throws IOException {
        Product savedProduct = productRepository.save(product);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = saveImage(file);
                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(savedProduct);
                    productImage.setImageUrl(fileName);
                    productImageRepository.save(productImage);
                }
            }
        }
        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Integer id, Product product, MultipartFile file) throws IOException {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setSale(product.getSale());
            existingProduct.setStockQuantity(product.getStockQuantity());
            existingProduct.setCategory(product.getCategory());
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

    @Transactional
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public List<ProductImage> getImagesByProductId(int productId) {
        return productImageRepository.findByProduct_ProductId(productId);
    }

    public String saveImage(MultipartFile file) throws IOException {
//        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String fileName = file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIRECTORY + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return fileName;
    }

}