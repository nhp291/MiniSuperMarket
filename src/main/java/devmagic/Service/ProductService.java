package devmagic.Service;

import devmagic.Model.Brand;
import devmagic.Model.Category;
import devmagic.Model.Product;
import devmagic.Model.Warehouse;
import devmagic.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Implement method to get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Implement method to get product by ID
    public Product getProductById(int id) {
        Optional<Product> product = productRepository.findById((Integer) id);
        return product.orElse(null);
    }

    // Implement method to get brand by ID
    public Brand getBrandById(int brandId) {
        // You need to implement the BrandRepository and its method to find a brand by ID
        // return brandRepository.findById(brandId).orElse(null);
        return null; // Replace with actual implementation
    }

    // Implement method to get category by ID
    public Category getCategoryById(int categoryId) {
        // You need to implement the CategoryRepository and its method to find a category by ID
        // return categoryRepository.findById(categoryId).orElse(null);
        return null; // Replace with actual implementation
    }

    // Implement method to get warehouse by ID
    public Warehouse getWarehouseById(int warehouseId) {
        // You need to implement the WarehouseRepository and its method to find a warehouse by ID
        // return warehouseRepository.findById(warehouseId).orElse(null);
        return null; // Replace with actual implementation
    }

    // Implement method to add a product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // Implement method to delete a product
    public boolean deleteProduct(int id) {
        if (productRepository.existsById((Integer) id)) {
            productRepository.deleteById((Integer) id);
            return true;
        }
        return false;
    }
}
