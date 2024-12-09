package devmagic.Service;

import devmagic.Dto.ProductDTO;
import devmagic.Model.*;
import devmagic.Reponsitory.ProductImageRepository;
import devmagic.Reponsitory.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) {
        return productRepository.findById(id).get();
    }

    public List<Product> findTop6Product() {
        Pageable limit = PageRequest.of(6, 6);  // Lấy trang đầu tiên với 6 sản phẩm
        return productRepository.findAll(limit).getContent();
    }

    public Page<Product> getall(Integer pageable) {
        Pageable page = PageRequest.of(pageable-1, 8);
        return this.productRepository.findAll(page);
    }

    public List<Product> searchProduct(String keyword) {
        return this.productRepository.searchProduct(keyword);
    }

    public Page<Product> seachProduct(String keyword, Integer pageNo) {
        List<Product> list = this.searchProduct(keyword);
        Pageable pageable = PageRequest.of(pageNo - 1, 8);  // Xử lý phân trang
        Integer start = (int) pageable.getOffset();
        Integer end = Math.min((int) ((pageable.getOffset() + pageable.getPageSize())), list.size()); // Đảm bảo 'end' không vượt quá 'list.size()'

        // Kiểm tra điều kiện trước khi lấy sublist
        if (start < end) {
            list = list.subList(start, end);
        } else {
            list = new ArrayList<>();  // Nếu không có sản phẩm nào trong phạm vi, trả về danh sách rỗng
        }

        return new PageImpl<>(list, pageable, this.searchProduct(keyword).size());
    }

    public Page<Product> getFiveAll(Integer pageable) {
        Pageable page = PageRequest.of(pageable +8, 8);
        return this.productRepository.findAll(page);
    }

    public List<Product> findByCategoryId(String cid) {
        return productRepository.findByCategoryId(cid);
    }

    public Page<Product> getForAll(Integer pageable) {
        Pageable page = PageRequest.of(pageable +4, 8);
        return this.productRepository.findAll(page);
    }

    public Page<Product> filterProductsByPrice(Double minPrice, Double maxPrice, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 8); // Số lượng sản phẩm mỗi trang
        if (minPrice == null) minPrice = 0.0;
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(Product product, List<MultipartFile> files) throws IOException {
        // Lưu sản phẩm vào DB trước
        Product savedProduct = productRepository.save(product);

        // Duyệt qua danh sách tệp hình ảnh
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // Sử dụng phương thức saveImage để xử lý hình ảnh
                    String fileName = saveImage(file);

                    // Tạo và lưu hình ảnh vào DB nếu chưa có
                    if (fileName != null) {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(savedProduct);
                        productImage.setImageUrl(fileName);
                        productImageRepository.save(productImage);
                    }
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
        String directoryPath = "src/main/resources/static/Image/imageUrl/";
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }

        // Lấy tên file gốc
        String originalFileName = file.getOriginalFilename();

        // Kiểm tra nếu file đã tồn tại
        Path imagePath = Paths.get(directoryPath + originalFileName);
        if (Files.exists(imagePath)) {
            System.out.println("File đã tồn tại: " + originalFileName);
            return originalFileName; // Trả về tên file
        }

        // Lưu file mới
        Files.write(imagePath, file.getBytes());
        System.out.println("File được lưu: " + originalFileName);
        return originalFileName;
    }

    public int getAvailableStock(Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            return product.getStockQuantity();
        }
        return 0; // Trả về 0 nếu sản phẩm không tồn tại
    }

    public Product convertToProduct(ProductDTO productDTO, Category category, Warehouse warehouse, Brand brand) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(BigDecimal.valueOf(productDTO.getPrice()));
        product.setSale(BigDecimal.valueOf(productDTO.getSale()));
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setCategory(category);
        product.setWarehouse(warehouse);
        product.setBrand(brand);
        product.setOrigin(productDTO.getOrigin());
        product.setUnit(productDTO.getUnit());
        return product;
    }

    public Page<Product> findBySearchCatBrand(String search, Integer cat, Integer brand, Pageable pageable) {
        return productRepository.findByNameContainingAndCategory_CategoryIdAndBrand_BrandId(search, cat, brand, pageable);
    }

    public Page<Product> findBySearchCat(String search, Integer cat, Pageable pageable) {
        return productRepository.findByNameContainingAndCategory_CategoryId(search, cat, pageable);
    }

    public Page<Product> findBySearchBrand(String search, Integer brand, Pageable pageable) {
        return productRepository.findByNameContainingAndBrand_BrandId(search, brand, pageable);
    }

    public Page<Product> findBySearch(String search, Pageable pageable) {
        return productRepository.findByNameContaining(search, pageable);
    }

    public Page<Product> findByCatBrand(Integer cat, Integer brand, Pageable pageable) {
        return productRepository.findByCategory_CategoryIdAndBrand_BrandId(cat, brand, pageable);
    }

    public Page<Product> findByCat(Integer cat, Pageable pageable) {
        return productRepository.findByCategory_CategoryId(cat, pageable);
    }

    public Page<Product> findByBrand(Integer brand, Pageable pageable) {
        return productRepository.findByBrand_BrandId(brand, pageable);
    }

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

}