package devmagic.Service;

import devmagic.Model.Brand;
import devmagic.Model.Category;
import devmagic.Model.Product;
import devmagic.Model.Warehouse;
import devmagic.Reponsitory.BrandRepository; // Import BrandRepository
import devmagic.Reponsitory.CategoryRepository; // Import CategoryRepository
import devmagic.Reponsitory.ProductRepository;
import devmagic.Reponsitory.WarehouseRepository; // Import WarehouseRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository; // Thêm BrandRepository

    @Autowired
    private CategoryRepository categoryRepository; // Thêm CategoryRepository

    @Autowired
    private WarehouseRepository warehouseRepository; // Thêm WarehouseRepository

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public boolean deleteProduct(int id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Thêm các phương thức mới
    public Brand getBrandById(int id) {
        Optional<Brand> brand = brandRepository.findById(id);
        return brand.orElse(null);
    }

    public Category getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElse(null);
    }

    public Warehouse getWarehouseById(int id) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(id);
        return warehouse.orElse(null);
    }
}







//package devmagic.Service;
//
//import devmagic.Model.Brand;
//import devmagic.Model.Category;
//import devmagic.Model.Product;
//import devmagic.Model.Warehouse;
//import devmagic.Reponsitory.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ProductService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    // Implement method to get all products
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    // Implement method to get product by ID
//    public Product getProductById(int id) {
//        Optional<Product> product = productRepository.findById((Integer) id);
//        return product.orElse(null);
//    }
//
//    // Implement method to get brand by ID
//    public Brand getBrandById(int brandId) {
//        // You need to implement the BrandRepository and its method to find a brand by ID
//        // return brandRepository.findById(brandId).orElse(null);
//        return null; // Replace with actual implementation
//    }
//
//    // Implement method to get category by ID
//    public Category getCategoryById(int categoryId) {
//        // You need to implement the CategoryRepository and its method to find a category by ID
//        // return categoryRepository.findById(categoryId).orElse(null);
//        return null; // Replace with actual implementation
//    }
//
//    // Implement method to get warehouse by ID
//    public Warehouse getWarehouseById(int warehouseId) {
//        // You need to implement the WarehouseRepository and its method to find a warehouse by ID
//        // return warehouseRepository.findById(warehouseId).orElse(null);
//        return null; // Replace with actual implementation
//    }
//
//    // Implement method to add a product
//    public Product addProduct(Product product) {
//        return productRepository.save(product);
//    }
//
//    // Implement method to delete a product
//    public boolean deleteProduct(int id) {
//        if (productRepository.existsById((Integer) id)) {
//            productRepository.deleteById((Integer) id);
//            return true;
//        }
//        return false;
//    }
//}
