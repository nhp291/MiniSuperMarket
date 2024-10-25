package devmagic.Service;

import devmagic.Model.Category;
import devmagic.Reponsitory.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoriesRepository;

    public List<Category> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoriesRepository.save(category);
    }

    public Category updateCategory(Integer id, Category category) {
        category.setCategoryId(id);
        return categoriesRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        categoriesRepository.deleteById(id);
    }
}

