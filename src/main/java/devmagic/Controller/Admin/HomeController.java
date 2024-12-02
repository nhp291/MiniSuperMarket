package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.AccountService;
import devmagic.Service.BrandService;
import devmagic.Service.CategoryService;
import devmagic.Utils.BaseController;
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
public class HomeController extends BaseController {

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

        Account account = (Account) session.getAttribute("Admin");
        if (account != null) {
            model.addAttribute("account", account);
            System.out.println("Image URL: " + account.getImageUrl());
        } else {
            System.out.println("Chưa có người dùng đăng nhập.");
            return "redirect:/user/login";
        }

        // Các thông tin khác
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


    @GetMapping("/MyProfile")
    public String MyProfile(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("Admin");
        if (account == null) {
            System.out.println("Session user attribute is null");
        } else {
            System.out.println("Session user: " + account.getUsername());
        }
        if (account != null) {
            model.addAttribute("account", account);
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("viewName", "admin/menu/MyProfile");
        } else {
            model.addAttribute("error", "Chưa đăng nhập");
            return "user/login";  // Nếu không có tài khoản thì chuyển hướng đến trang login
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

        Account currentAccount = (Account) session.getAttribute("Admin");
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
            String contentType = imageFile.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uploadDir = "src/main/resources/static/Image/imageProfile/";
                Path uploadPath = Paths.get(uploadDir);
                Path filePath = uploadPath.resolve(fileName);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Nếu file đã tồn tại, chỉ cập nhật tên trong database
                if (Files.exists(filePath)) {
                    currentAccount.setImageUrl(fileName);
                } else {
                    // Nếu file chưa tồn tại, lưu file mới
                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        currentAccount.setImageUrl(fileName);
                    } catch (IOException e) {
                        throw new IOException("Không thể lưu hình ảnh: " + fileName, e);
                    }
                }
            } else {
                model.addAttribute("error", "Tệp không phải là hình ảnh hợp lệ.");
                return "admin/layout";
            }
        } else if (currentAccount.getImageUrl() == null) {
            currentAccount.setImageUrl("User.png");
        }


        // Lưu thông tin tài khoản đã thay đổi
        accountService.saveAccount(currentAccount);

        // Cập nhật thông tin trong session
        session.setAttribute("Admin", currentAccount);

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

    @GetMapping("/GeneralSetting")
    public String GeneralSettings(Model model) {
        model.addAttribute("pageTitle", "General Settings");
        model.addAttribute("viewName", "admin/menu/GeneralSettings");
        return "admin/layout";
    }

}
