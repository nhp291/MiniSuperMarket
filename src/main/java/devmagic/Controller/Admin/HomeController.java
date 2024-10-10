package devmagic.Controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Admin")
public class HomeController {

    @GetMapping("/Home")
    public String Home(Model model) {
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

    @GetMapping("/EditRole")
    public String EditRole(Model model) {
        model.addAttribute("pageTitle", "Home Page");
        model.addAttribute("viewName", "admin/menu/EditRole");
        return "admin/layout";
    }

    @GetMapping("/ProductList")
    public String ProductList(Model model) {
        model.addAttribute("pageTitle", "Product List Page");
        model.addAttribute("viewName", "admin/menu/ProductList");
        return "admin/layout";
    }

    @GetMapping("/AddProduct")
    public String AddProduct(Model model) {
        model.addAttribute("pageTitle", "Add Product Page");
        model.addAttribute("viewName", "admin/menu/AddProduct");
        return "admin/layout";
    }

    @GetMapping("/ProductDetail")
    public String ProductDetail(Model model) {
        model.addAttribute("pageTitle", " Product Detail Page");
        model.addAttribute("viewName", "admin/menu/ProductDetail");
        return "admin/layout";
    }

    @GetMapping("/CategoryList")
    public String CategoryList(Model model) {
        model.addAttribute("pageTitle", "Category List Page");
        model.addAttribute("viewName", "admin/menu/CategoryList");
        return "admin/layout";
    }

    @GetMapping("/AddCategory")
    public String AddCategory(Model model) {
        model.addAttribute("pageTitle", "add Category Page");
        model.addAttribute("viewName", "admin/menu/AddCategory");
        return "admin/layout";
    }

    @GetMapping("/BrandList")
    public String BrandList(Model model) {
        model.addAttribute("pageTitle", "Brand List Page");
        model.addAttribute("viewName", "admin/menu/BrandList");
        return "admin/layout";
    }

    @GetMapping("/AddBrand")
    public String AddBrand(Model model) {
        model.addAttribute("pageTitle", "Add Brand Page");
        model.addAttribute("viewName", "admin/menu/AddBrand");
        return "admin/layout";
    }

    @GetMapping("/WareHouse")
    public String WareHouse(Model model) {
        model.addAttribute("pageTitle", "WareHouse Page");
        model.addAttribute("viewName", "admin/menu/WareHouse");
        return "admin/layout";
    }

    @GetMapping("/AccountList")
    public String AccountList(Model model) {
        model.addAttribute("pageTitle", "Account List Page");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }

    @GetMapping("/AddAccount")
    public String AddAccount(Model model) {
        model.addAttribute("pageTitle", "Add Account Page");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    @GetMapping("/PromotionList")
    public String PromotionList(Model model) {
        model.addAttribute("pageTitle", "Promotion List Page");
        model.addAttribute("viewName", "admin/menu/PromotionList");
        return "admin/layout";
    }

    @GetMapping("/AddPromotion")
    public String AddQuotation(Model model) {
        model.addAttribute("pageTitle", "Add Promotion Page");
        model.addAttribute("viewName", "admin/menu/AddPromotion");
        return "admin/layout";
    }

    @GetMapping("/OrderList")
    public String OrderList(Model model) {
        model.addAttribute("pageTitle", "Order List Page");
        model.addAttribute("viewName", "admin/menu/OrderList");
        return "admin/layout";
    }

    @GetMapping("/error-404")
    public String error_404(Model model) {
        model.addAttribute("pageTitle", "error-404 Page");
        model.addAttribute("viewName", "admin/menu/error-404");
        return "admin/layout";
    }

    @GetMapping("/error-500")
    public String error_500(Model model) {
        model.addAttribute("pageTitle", "error-500 Page");
        model.addAttribute("viewName", "admin/menu/error-500");
        return "admin/layout";
    }

    @GetMapping("/ChartApex")
    public String ChartApex(Model model) {
        model.addAttribute("pageTitle", "Chart-Apex Page");
        model.addAttribute("viewName", "admin/menu/chart-apex");
        return "admin/layout";
    }

    @GetMapping("/ChartJs")
    public String ChartJs(Model model) {
        model.addAttribute("pageTitle", "Chart-js Page");
        model.addAttribute("viewName", "admin/menu/chart-js");
        return "admin/layout";
    }

    @GetMapping("/ChartMorris")
    public String ChartMorris(Model model) {
        model.addAttribute("pageTitle", "Chart-Morris Page");
        model.addAttribute("viewName", "admin/menu/chart-morris");
        return "admin/layout";
    }

    @GetMapping("/ChartFlot")
    public String ChartFlot(Model model) {
        model.addAttribute("pageTitle", "Chart-Flot Page");
        model.addAttribute("viewName", "admin/menu/chart-flot");
        return "admin/layout";
    }

    @GetMapping("/ChartPeity")
    public String ChartPeity(Model model) {
        model.addAttribute("pageTitle", "Chart-Peity Page");
        model.addAttribute("viewName", "admin/menu/chart-peity");
        return "admin/layout";
    }

    @GetMapping("/Profile")
    public String Profile(Model model) {
        model.addAttribute("pageTitle", "Profile Page");
        model.addAttribute("viewName", "admin/menu/MyProfile");
        return "admin/layout";
    }

    @GetMapping("/Generalsettings")
    public String Generalsettings(Model model) {
        model.addAttribute("pageTitle", "Generalsettings Page");
        model.addAttribute("viewName", "admin/menu/generalsettings");
        return "admin/layout";
    }

}

