package devmagic.Controller.Admin;

import devmagic.Model.Category;
import devmagic.Model.Product;
import devmagic.Reponsitory.CategoryRepository;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.CategoryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // Hiển thị danh sách danh mục
    @GetMapping("/CategoryList")
    public String categoryList(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 10; // Number of categories per page
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Get the paginated categories
        Page<Category> categoryPage = categoryService.getCategoriesPage(pageable);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
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

        // Đường dẫn thư mục lưu trữ ảnh
        String projectImageDir = "src/main/resources/static/Image/imageUrl/";
        Path uploadPath = Paths.get(projectImageDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa tồn tại
        }

        if (!imageFile.isEmpty()) {
            // Lấy tên file mới
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);

            // Kiểm tra nếu file đã tồn tại
            if (Files.exists(filePath)) {
                // Nếu file đã tồn tại, chỉ cần cập nhật tên vào DB
                category.setImageUrl(fileName);
            } else {
                // Nếu file chưa tồn tại, lưu file mới và cập nhật tên
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IOException("Không thể lưu tệp hình ảnh: " + fileName, e);
                }
                category.setImageUrl(fileName);
            }
        } else if (id != null) {
            // Nếu không upload file, giữ lại ảnh cũ khi chỉnh sửa
            Optional<Category> existingCategory = categoryService.findById(id);
            existingCategory.ifPresent(c -> category.setImageUrl(c.getImageUrl()));
        }

        // Thực hiện lưu hoặc cập nhật danh mục
        if (id != null) {
            categoryService.updateCategory(id, category);
        } else {
            categoryService.createCategory(category);
        }

        // Chuyển hướng về danh sách danh mục
        return "redirect:/Categories/CategoryList";
    }

    // Xóa danh mục và cập nhật các sản phẩm
    @GetMapping("/DeleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") int id) {
        categoryService.deleteCategory(id); // Gọi service để xóa danh mục và sản phẩm
        return "redirect:/Categories/CategoryList"; // Chuyển hướng về danh sách danh mục
    }

}
