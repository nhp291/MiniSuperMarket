package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.AccountService;
import devmagic.Service.BrandService;
import devmagic.Service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/Admin")
public class HomeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/Home")
    public String Home(Model model, HttpSession session) {
        // Lấy tài khoản người dùng từ session
        Account account = (Account) session.getAttribute("user");

        // Truyền thông tin tài khoản vào model
        if (account != null) {
            model.addAttribute("account", account);
        }

        long productCount = productRepository.countProducts();
        model.addAttribute("productCount", productCount);

        long totalAccounts = accountService.getTotalAccounts();
        model.addAttribute("totalAccounts", totalAccounts);

        long categoryCount = categoryService.getTotalCategories();
        model.addAttribute("categoryCount", categoryCount);

        long brandCount = brandService.getTotalBrands();
        model.addAttribute("brandCount", brandCount);

        model.addAttribute("pageTitle", "Home Page");
        model.addAttribute("viewName", "admin/index");
        return "admin/layout";
    }


    @GetMapping("/GeneralSettings")
    public String GeneralSettings(Model model) {
        model.addAttribute("pageTitle", "General Settings");
        model.addAttribute("viewName", "admin/menu/GeneralSettings");
        return "admin/layout";
    }

    @GetMapping("/MyProfile")
    public String MyProfile(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("user");

        if (account != null) {
            model.addAttribute("account", account);
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("viewName", "admin/menu/MyProfile");
        } else {
            model.addAttribute("error", "Chưa đăng nhập");
            return "user/login";  // Nếu chưa đăng nhập, chuyển hướng tới trang login
        }

        return "admin/layout";
    }

    @PostMapping("/UpdateProfile")
    public String updateProfile(@ModelAttribute("account") Account account,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                HttpSession session,
                                Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("viewName", "admin/menu/MyProfile");
            return "admin/layout";
        }

        Account currentAccount = (Account) session.getAttribute("user");
        if (currentAccount == null) {
            model.addAttribute("error", "Bạn cần đăng nhập trước khi cập nhật thông tin.");
            return "user/login";
        }

        // Cập nhật thông tin người dùng
        currentAccount.setUsername(account.getUsername());
        currentAccount.setEmail(account.getEmail());
        currentAccount.setPhoneNumber(account.getPhoneNumber());
        currentAccount.setAddress(account.getAddress());

        // Xử lý ảnh đại diện nếu có
        if (!imageFile.isEmpty()) {
            // Lấy MIME type của tệp
            String contentType = imageFile.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uploadDir = "src/main/resources/static/Image/imageProfile/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    currentAccount.setImageUrl(fileName);
                } catch (IOException e) {
                    throw new IOException("Không thể lưu hình ảnh: " + fileName, e);
                }
            } else {
                throw new IOException("Tệp không phải là hình ảnh hợp lệ");
            }
        }

        // Lưu thông tin tài khoản đã thay đổi
        accountService.saveAccount(currentAccount);

        // Cập nhật thông tin trong session
        session.setAttribute("user", currentAccount);

        model.addAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/Admin/MyProfile"; // Quay lại trang hồ sơ
    }

    // Xử lý khi người dùng click logout
    @PostMapping("/Admin/Logout")
    public String logout(HttpSession session) {
        // Xóa thông tin người dùng khỏi session
        session.invalidate(); // Hoặc bạn có thể xóa thông tin cụ thể: session.removeAttribute("account");

        // Sau khi logout xong, chuyển hướng đến trang chủ
        return "redirect:/layout/Home";
    }

    @GetMapping("/Admin/Home")
    public String home() {
        return "home"; // Trả về tên view trang Home (có thể là home.html hoặc trang khác tùy theo cấu hình của bạn)
    }

}
