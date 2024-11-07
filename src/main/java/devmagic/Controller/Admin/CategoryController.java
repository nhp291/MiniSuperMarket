package devmagic.Controller.Admin;

import devmagic.Model.Category;
import devmagic.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/Categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách danh mục
    @GetMapping("/CategoryList")
    public String categoryList(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Danh sách danh mục");
        model.addAttribute("viewName", "admin/menu/CategoryList");
        return "admin/layout";
    }

    // Hiển thị form thêm danh mục
    @GetMapping("/AddCategory")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Thêm danh mục");
        model.addAttribute("viewName", "admin/menu/AddCategory");
        return "admin/layout";
    }

    // Xử lý thêm danh mục
    @PostMapping("/AddCategory")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm danh mục");
            model.addAttribute("viewName", "admin/menu/AddCategory");
            return "admin/layout";
        }

        String projectImageDir = "src/main/resources/static/Image/imageUrl/";

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get(projectImageDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Không thể lưu tệp hình ảnh: " + fileName, e);
            }
            category.setImageUrl(fileName);
        } else {
            category.setImageUrl("/Image/imageUrl/Default.jpg");
        }

        categoryService.createCategory(category);
        return "redirect:/Categories/CategoryList";
    }

    // Hiển thị form chỉnh sửa danh mục
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable("id") Integer id, Model model) {
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (categoryOptional.isPresent()) {
            model.addAttribute("category", categoryOptional.get());
            model.addAttribute("pageTitle", "Chỉnh sửa danh mục");
            model.addAttribute("viewName", "admin/menu/AddCategory");
            return "admin/layout";
        } else {
            model.addAttribute("error", "Không tìm thấy danh mục.");
            return "redirect:/Categories/CategoryList";
        }
    }

    // Xử lý cập nhật danh mục
    @PostMapping("/update")
    public String updateCategory(@Valid @ModelAttribute("category") Category category,
                                 BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Chỉnh sửa danh mục");
            model.addAttribute("viewName", "admin/menu/AddCategory");
            return "admin/layout";
        }

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String projectImageDir = "src/main/resources/static/Image/imageUrl/";
            Path uploadPath = Paths.get(projectImageDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Không thể lưu tệp hình ảnh: " + fileName, e);
            }
            category.setImageUrl( fileName);
        } else {
            Optional<Category> existingCategory = categoryService.findById(category.getCategoryId());
            category.setImageUrl(existingCategory.map(Category::getImageUrl).orElse("/Image/imageUrl/Default.jpg"));
        }

        categoryService.updateCategory(category.getCategoryId(), category);
        return "redirect:/Categories/CategoryList";
    }

    // Xóa danh mục
    @PostMapping("/DeleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, Model model) {
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (categoryOptional.isPresent()) {
            categoryService.deleteCategory(id);
        } else {
            model.addAttribute("error", "Không tìm thấy danh mục.");
        }
        return "redirect:/Categories/CategoryList";
    }
}
