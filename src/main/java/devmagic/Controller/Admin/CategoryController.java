package devmagic.Controller.Admin;

import devmagic.Model.Category;
import devmagic.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách danh mục
    @GetMapping("/CategoryList")
    public String categoryList(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        if (categories == null) {
            categories = new ArrayList<>(); // Đảm bảo danh sách không NULL
        }
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Danh sách danh mục");
        model.addAttribute("viewName", "admin/menu/CategoryList");
        return "admin/layout";
    }

    // Hiển thị form thêm danh mục
    @GetMapping("/AddCategory")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Danh mục loại sản phẩm");
        model.addAttribute("viewName", "admin/menu/AddCategory");
        return "admin/layout";
    }

    // Xử lý việc tạo danh mục mới
    @PostMapping("/AddCategory")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              @RequestParam("imageFile") MultipartFile imageFile) {
        // Xử lý lưu hình ảnh
        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile); // Hàm lưu hình ảnh
            category.setImageUrl(imagePath);
        }
        categoryService.createCategory(category);
        return "redirect:/Categories/CategoryList";
    }

    // Hiển thị form chỉnh sửa danh mục
    @GetMapping("/EditCategory/{id}")
    public String editCategoryForm(@PathVariable Integer id, Model model) {
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (categoryOptional.isPresent()) {
            model.addAttribute("category", categoryOptional.get());
            model.addAttribute("pageTitle", "Chỉnh sửa danh mục");
            model.addAttribute("viewName", "admin/menu/AddCategory");
            return "admin/layout";
        } else {
            return "redirect:/Categories/CategoryList"; // Quay về danh sách nếu không tìm thấy
        }
    }

    // Xử lý việc cập nhật danh mục
    @PostMapping("/UpdateCategory/{id}")
    public String updateCategory(@PathVariable Integer id,
                                 @Valid @ModelAttribute("category") Category category,
                                 @RequestParam("imageFile") MultipartFile imageFile) {
        // Xử lý lưu hình ảnh
        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile); // Hàm lưu hình ảnh
            category.setImageUrl(imagePath);
        }
        categoryService.updateCategory(id, category);
        return "redirect:/Categories/CategoryList";
    }

    // Xóa danh mục theo ID
    @GetMapping("/DeleteCategory/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return "redirect:/Categories/CategoryList";
    }

    // Hàm lưu hình ảnh
    private String saveImage(MultipartFile imageFile) {
        String directory = "C:/Phong/Intellij_IDE/MiniSuperMarket/src/main/resources/static/Image/";
        String imagePath = directory + imageFile.getOriginalFilename();
        try {
            File file = new File(imagePath);
            imageFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Image/" + imageFile.getOriginalFilename(); // Trả về đường dẫn tương đối
    }
}
