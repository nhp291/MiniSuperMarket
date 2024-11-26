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
import java.util.Optional;

@Controller
@RequestMapping("/Brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Hiển thị danh sách thương hiệu
    @GetMapping("/BrandList")
    public String getBrandList(Model model) {
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("pageTitle", "Danh sách thương hiệu");
        model.addAttribute("viewName", "admin/menu/BrandList");
        return "admin/layout";
    }

    // Hiển thị form thêm/cập nhật thương hiệu
    @GetMapping({"/AddBrand", "/EditBrand/{id}"})
    public String brandForm(@PathVariable(value = "id", required = false) Integer id, Model model) {
        Brand brand = id != null ? brandService.getBrandById(id).orElse(new Brand()) : new Brand();
        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", id != null ? "Cập nhật thương hiệu" : "Thêm thương hiệu mới");
        model.addAttribute("viewName", "admin/menu/AddBrand");
        return "admin/layout";
    }

    // Xử lý thêm/cập nhật thương hiệu
    @PostMapping({"/AddBrand", "/UpdateBrand/{id}"})
    public String saveBrand(@PathVariable(value = "id", required = false) Integer id,
                            @Valid @ModelAttribute("brand") Brand brand,
                            BindingResult result,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", id != null ? "Cập nhật thương hiệu" : "Thêm thương hiệu mới");
            model.addAttribute("viewName", "admin/menu/AddBrand");
            return "admin/layout";
        }

        // Xử lý đường dẫn lưu ảnh
        String uploadDir = "src/main/resources/static/Image/imageUrl/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Trường hợp upload ảnh mới
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String newFileName = System.currentTimeMillis() + "_" + fileName;
            Path newFilePath = uploadPath.resolve(newFileName);

            // Kiểm tra nếu ảnh đã tồn tại trong thư mục
            Path existingFilePath = uploadPath.resolve(fileName);
            if (Files.exists(existingFilePath)) {
                // Nếu ảnh đã có, chỉ cần cập nhật tên vào cơ sở dữ liệu
                brand.setImageUrl(fileName);
            } else {
                // Lưu ảnh mới nếu chưa tồn tại
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IOException("Không thể lưu ảnh: " + newFileName, e);
                }
                // Cập nhật tên file ảnh mới vào đối tượng Brand
                brand.setImageUrl(newFileName);
            }

        } else if (id != null) {
            // Nếu không upload ảnh mới, giữ nguyên ảnh cũ nếu có
            Optional<Brand> existingBrand = brandService.getBrandById(id);
            existingBrand.ifPresent(b -> brand.setImageUrl(b.getImageUrl()));
        } else {
            // Nếu không upload ảnh và thêm mới, để giá trị imageUrl = null
            brand.setImageUrl(null);
        }

        // Lưu thương hiệu (thêm mới hoặc cập nhật)
        if (id != null) {
            brandService.updateBrand(id, brand);
        } else {
            brandService.createBrand(brand);
        }

        return "redirect:/Brand/BrandList";
    }

    // Xóa thương hiệu
    @GetMapping("/DeleteBrand/{id}")
    public String deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return "redirect:/Brand/BrandList";
    }
}
