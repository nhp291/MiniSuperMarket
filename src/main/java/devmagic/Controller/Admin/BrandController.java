package devmagic.Controller.Admin;

import devmagic.Model.Brand;
import devmagic.Service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Hiển thị danh sách thương hiệu
    @GetMapping("/BrandList")
    public String getBrandList(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Danh sách thương hiệu");
        model.addAttribute("viewName", "admin/menu/BrandList");
        return "admin/layout";
    }

    // Hiển thị form thêm thương hiệu
    @GetMapping("/AddBrand")
    public String addBrandForm(Model model) {
        Brand brand = new Brand();  // Đảm bảo brand mới không có brandId
        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Thêm thương hiệu");
        model.addAttribute("viewName", "admin/menu/AddBrand");
        return "admin/layout";
    }

    // Xử lý thêm thương hiệu
    @PostMapping("/AddBrand")
    public String addBrand(@Valid @ModelAttribute("brand") Brand brand,
                           BindingResult result,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm thương hiệu");
            model.addAttribute("viewName", "admin/menu/AddBrand");
            return "admin/layout";
        }

        // Lưu ảnh và thương hiệu mới
        handleImageUpload(brand, imageFile);
        brandService.createBrand(brand); // Lưu brand mới vào cơ sở dữ liệu
        return "redirect:/Brand/BrandList";
    }

    // Hiển thị form chỉnh sửa thương hiệu
    @GetMapping("/EditBrand/{id}")
    public String editBrandForm(@PathVariable Integer id, Model model) {
        Optional<Brand> brandOptional = brandService.getBrandById(id);
        if (brandOptional.isPresent()) {
            model.addAttribute("brand", brandOptional.get());
            model.addAttribute("pageTitle", "Cập nhật thương hiệu");
            model.addAttribute("viewName", "admin/menu/AddBrand");
            return "admin/layout";
        } else {
            model.addAttribute("error", "Không tìm thấy thương hiệu.");
            return "redirect:/Brand/BrandList";
        }
    }

    // Xử lý cập nhật thương hiệu
    @PostMapping("/UpdateBrand/{id}")
    public String updateBrand(@PathVariable Integer id,
                              @Valid @ModelAttribute("brand") Brand brand,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Cập nhật thương hiệu");
            model.addAttribute("viewName", "admin/menu/AddBrand");
            return "admin/layout";
        }

        // Lưu ảnh và thương hiệu đã cập nhật
        handleImageUpload(brand, imageFile);
        brandService.updateBrand(id, brand); // Cập nhật thương hiệu
        return "redirect:/Brand/BrandList";
    }

    private void handleImageUpload(Brand brand, MultipartFile imageFile) throws IOException {
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
                throw new IOException("Không thể lưu ảnh", e);
            }

            brand.setImageUrl(fileName);
        }
    }

    // Xóa thương hiệu
    @GetMapping("/DeleteBrand/{id}")
    public String deleteBrand(@PathVariable Integer id) {
        if (brandService.getBrandById(id).isPresent()) {
            brandService.deleteBrand(id);
        }
        return "redirect:/Brand/BrandList";
    }
}
