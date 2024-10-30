package devmagic.Controller.Admin;

import devmagic.Model.Category;
import devmagic.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoriesService;

    @GetMapping("/CategoryList")
    public String CategoryList(Model model) {
        model.addAttribute("pageTitle", "Category List Page");
        model.addAttribute("viewName", "admin/menu/CategoryList");
        return "admin/layout";
    }

    @GetMapping("/AddCategory")
    public String AddCategory(Model model) {
        model.addAttribute("pageTitle", "add Category Page");
        model.addAttribute("viewName", "admin/menu/AddCategory");
        return "admin/layout";
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoriesService.getAllCategories();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoriesService.createCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return categoriesService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

