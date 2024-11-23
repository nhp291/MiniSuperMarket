package devmagic.Controller.User;

import devmagic.Model.Product;
import devmagic.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeUserController {
    @Autowired
    private ProductService productService;

    @RequestMapping("/layout/Home")
    public String Home(
            Model model,
            @Param("keyword") String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice) {

        // Lấy thông tin người dùng
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();

        System.out.print("Role nè: " + role);

        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        // Lấy danh sách sản phẩm
        Page<Product> list;
        List<Product> topProducts = productService.finTop6Product();
        Page<Product> paginatedProducts = productService.getForAll(pageNo);

        // Xử lý tìm kiếm hoặc lọc giá
        if (keyword != null) {
            list = productService.seachProduct(keyword, pageNo);
            model.addAttribute("keyword", keyword);
        } else if (minPrice != null || maxPrice != null) {
            list = productService.filterProductsByPrice(minPrice, maxPrice, pageNo);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        } else {
            list = productService.getall(pageNo); // Không có bộ lọc, lấy toàn bộ sản phẩm
        }

        model.addAttribute("totalPage", list.getTotalPages());
        model.addAttribute("currenPage", pageNo);
        model.addAttribute("product", list);
        model.addAttribute("product1", paginatedProducts);
        model.addAttribute("product2", topProducts);

        return "layout/Home";
    }


    @RequestMapping("/layout/Product")
    public String Product(Model model, @RequestParam("cid") Optional<String> cid) {
        if (cid.isPresent()) {
            List<Product> list = productService.findByCategoryId(cid.get());
            model.addAttribute("product", list);
        } else {
            List<Product> list = productService.findAll();
            model.addAttribute("product", list);
        }
        return "layout/Product";
    }

    @RequestMapping("/product/detail/{id}")
    public String ProductDetail(Model model, @PathVariable("id") Integer id) {
        Product item = productService.findById(id);
        List<Product> topProducts = productService.finTop6Product();
        model.addAttribute("item", item);
        model.addAttribute("product2", topProducts);
        return "layout/ProductDetail";
    }

    @GetMapping("/user/login")
    public String login(Model model) {
        return "user/login";
    }

    @GetMapping("/user/contact")
    public String contact(Model model) {
        return "user/contact";
    }

    @GetMapping("/user/register")
    public String register(Model model) {
        return "user/register";
    }

    @GetMapping("/ForgotPassword")
    public String ForgotPassword(Model model) {
        return "ForgotPassword";
    }

    // Phương thức lấy thông tin người dùng đang đăng nhập
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }

    // Phương thức lấy vai trò của người dùng
    private String getAuthenticatedRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse(null); // Trả về vai trò đầu tiên nếu có
        }
        return null;
    }
}