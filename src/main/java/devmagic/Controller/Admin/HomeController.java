package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Reponsitory.CategoryRepository;
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
import java.util.List;

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
    public String Home(Model model) {

        long productCount = productRepository.countProducts(); // Sử dụng phương thức countProducts() như đã tạo
        model.addAttribute("productCount", productCount);

        long totalAccounts = accountService.getTotalAccounts();  // Gọi phương thức để đếm số lượng Account
        model.addAttribute("totalAccounts", totalAccounts);

        long categoryCount = categoryService.getTotalCategories(); // Gọi phương thức để đếm số lượng Category
        model.addAttribute("categoryCount", categoryCount);

        long brandCount = brandService.getTotalBrands(); // Gọi phương thức để đếm số lượng Brand
        model.addAttribute("brandCount", brandCount);

        model.addAttribute("pageTitle", "Home Page");
        model.addAttribute("viewName", "admin/index");
        return "admin/layout";
    }


    @GetMapping("/GeneralSettings")
    public String GeneralSettings(Model model) {
        model.addAttribute("pageTitle", "My Profile Page");
        model.addAttribute("viewName", "admin/menu/GeneralSettings");
        return "admin/layout";
    }

    @GetMapping("/MyProfile")
    public String MyProfile(Model model, HttpSession session) {
        // Lấy thông tin tài khoản từ session
        Account account = (Account) session.getAttribute("user");

        if (account != null) {
            // Truyền thông tin tài khoản vào model
            model.addAttribute("account", account);
            model.addAttribute("pageTitle", "My Profile Page");
            model.addAttribute("viewName", "admin/menu/MyProfile");
        } else {
            model.addAttribute("error", "Chưa đăng nhập");
            return "user/login";  // Nếu không có thông tin tài khoản trong session, chuyển đến trang login
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
            model.addAttribute("pageTitle", "My Profile Page");
            model.addAttribute("viewName", "admin/menu/MyProfile");
            return "admin/layout";
        }   

        // Lấy tài khoản từ session
        Account currentAccount = (Account) session.getAttribute("user");
        if (currentAccount == null) {
            model.addAttribute("error", "Bạn cần đăng nhập trước khi cập nhật thông tin.");
            return "user/login";
        }

        // Cập nhật thông tin từ form
        currentAccount.setUsername(account.getUsername());
        currentAccount.setEmail(account.getEmail());
        currentAccount.setPhoneNumber(account.getPhoneNumber());
        currentAccount.setAddress(account.getAddress());

        // Xử lý cập nhật ảnh đại diện nếu có
        if (!imageFile.isEmpty()) {
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
        }

        // Lưu thay đổi vào cơ sở dữ liệu
        accountService.saveAccount(currentAccount);

        // Cập nhật session
        session.setAttribute("user", currentAccount);

        model.addAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/Admin/MyProfile";
    }


}








//    @GetMapping("/WareHouse")
//    public String WareHouse(Model model) {
//        model.addAttribute("pageTitle", "WareHouse Page");
//        model.addAttribute("viewName", "admin/menu/WareHouse");
//        return "admin/layout";
//    }
//
//
//
//    @GetMapping("/PromotionList")
//    public String PromotionList(Model model) {
//        model.addAttribute("pageTitle", "Promotion List Page");
//        model.addAttribute("viewName", "admin/menu/PromotionList");
//        return "admin/layout";
//    }
//
//    @GetMapping("/AddPromotion")
//    public String AddQuotation(Model model) {
//        model.addAttribute("pageTitle", "Add Promotion Page");
//        model.addAttribute("viewName", "admin/menu/AddPromotion");
//        return "admin/layout";
//    }
//
//
//
//    @GetMapping("/error-404")
//    public String error_404(Model model) {
//        model.addAttribute("pageTitle", "error-404 Page");
//        model.addAttribute("viewName", "admin/menu/error-404");
//        return "admin/layout";
//    }
//
//    @GetMapping("/error-500")
//    public String error_500(Model model) {
//        model.addAttribute("pageTitle", "error-500 Page");
//        model.addAttribute("viewName", "admin/menu/error-500");
//        return "admin/layout";
//    }
//
//    @GetMapping("/ChartApex")
//    public String ChartApex(Model model) {
//        model.addAttribute("pageTitle", "Chart-Apex Page");
//        model.addAttribute("viewName", "admin/menu/chart-apex");
//        return "admin/layout";
//    }
//
//    @GetMapping("/ChartJs")
//    public String ChartJs(Model model) {
//        model.addAttribute("pageTitle", "Chart-js Page");
//        model.addAttribute("viewName", "admin/menu/chart-js");
//        return "admin/layout";
//    }
//
//    @GetMapping("/ChartMorris")
//    public String ChartMorris(Model model) {
//        model.addAttribute("pageTitle", "Chart-Morris Page");
//        model.addAttribute("viewName", "admin/menu/chart-morris");
//        return "admin/layout";
//    }
//
//    @GetMapping("/ChartFlot")
//    public String ChartFlot(Model model) {
//        model.addAttribute("pageTitle", "Chart-Flot Page");
//        model.addAttribute("viewName", "admin/menu/chart-flot");
//        return "admin/layout";
//    }
//
//    @GetMapping("/ChartPeity")
//    public String ChartPeity(Model model) {
//        model.addAttribute("pageTitle", "Chart-Peity Page");
//        model.addAttribute("viewName", "admin/menu/chart-peity");
//        return "admin/layout";
//    }
//
//    @GetMapping("/Profile")
//    public String Profile(Model model) {
//        model.addAttribute("pageTitle", "Profile Page");
//        model.addAttribute("viewName", "admin/menu/profile");
//        return "admin/layout";
//    }
//
//    @GetMapping("/Generalsettings")
//    public String Generalsettings(Model model) {
//        model.addAttribute("pageTitle", "Generalsettings Page");
//        model.addAttribute("viewName", "admin/menu/generalsettings");
//        return "admin/layout";
//    }
//}

