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

    // Hiển thị form thêm hoặc sửa danh mục
    @GetMapping({"/AddCategory", "/edit/{id}"})
    public String showCategoryForm(@PathVariable(value = "id", required = false) Integer id, Model model) {
        Category category = id != null ? categoryService.findById(id).orElse(new Category()) : new Category();
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", id != null ? "Cập nhật danh mục" : "Thêm danh mục");
        model.addAttribute("viewName", "admin/menu/AddCategory");
        return "admin/layout";
    }

    // Xử lý thêm hoặc cập nhật danh mục
    @PostMapping({"/AddCategory", "/update/{id}"})
    public String saveCategory(@PathVariable(value = "id", required = false) Integer id,
                               @Valid @ModelAttribute("category") Category category,
                               BindingResult result,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               Model model) throws IOException {

        // Kiểm tra lỗi valid
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", id != null ? "Cập nhật danh mục" : "Thêm danh mục");
            model.addAttribute("viewName", "admin/menu/AddCategory");
            return "admin/layout";
        }

        // Nếu có ảnh mới, lưu lại ảnh
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
            category.setImageUrl(fileName); // Cập nhật đường dẫn ảnh mới
        } else if (id != null) {
            // Nếu không có ảnh mới, giữ lại ảnh cũ khi chỉnh sửa
            Optional<Category> existingCategory = categoryService.findById(id);
            existingCategory.ifPresent(c -> category.setImageUrl(c.getImageUrl()));
        }

        // Nếu có id, thực hiện cập nhật, nếu không thì tạo mới
        if (id != null) {
            Optional<Category> existingCategory = categoryService.findById(id);
            if (existingCategory.isPresent()) {
                categoryService.updateCategory(id, category); // Cập nhật danh mục
            } else {
                // Nếu không tìm thấy danh mục với id đó, có thể báo lỗi hoặc tạo danh mục mới
                categoryService.createCategory(category);
            }
        } else {
            categoryService.createCategory(category); // Thêm mới nếu không có id
        }

        // Chuyển hướng về danh sách danh mục
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
