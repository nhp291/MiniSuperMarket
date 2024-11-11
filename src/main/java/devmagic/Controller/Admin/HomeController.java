package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Reponsitory.CategoryRepository;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.AccountService;
import devmagic.Service.BrandService;
import devmagic.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String MyProfile(Model model) {
        model.addAttribute("pageTitle", "My Profile Page");
        model.addAttribute("viewName", "admin/menu/MyProfile");
        return "admin/layout";
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

