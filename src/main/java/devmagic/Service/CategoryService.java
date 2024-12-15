package devmagic.Service;

import devmagic.Model.Category;
import devmagic.Model.Product;
import devmagic.Reponsitory.CategoryRepository;
import devmagic.Reponsitory.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(int id, Category category) {
        category.setCategoryId(id);
        return categoryRepository.save(category);
    }

    public long getTotalCategories() {
        return categoryRepository.count();  // Gọi phương thức để đếm số lượng Category
    }


    public Page<Category> getCategoriesPage(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional
    public void deleteCategory(int categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            // Lấy danh mục cần xóa
            Category category = categoryOptional.get();

            // Loại bỏ liên kết giữa sản phẩm và danh mục
            List<Product> products = productRepository.findByCategory(category);
            for (Product product : products) {
                product.setCategory(null); // Đặt category của sản phẩm thành null
            }
            productRepository.saveAll(products); // Lưu lại các sản phẩm đã cập nhật

            // Xóa danh mục
            categoryRepository.delete(category);
        } else {
            throw new IllegalArgumentException("Category với ID " + categoryId + " không tồn tại");
        }
    }

}
