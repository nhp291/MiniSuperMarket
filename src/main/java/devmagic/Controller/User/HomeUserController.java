package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Model.Cart;
import devmagic.Model.Product;
import devmagic.Service.AccountService;
import devmagic.Service.CartService;
import devmagic.Service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeUserController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CartService cartService;

    @RequestMapping("/layout/Home")
    public String Home(
            Model model,
            @Param("keyword") String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            HttpServletRequest request) {

        // Lấy thông tin người dùng từ session
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();
        Integer accountId = getAccountIdFromSession(request);

        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }

        // Lấy danh sách sản phẩm
        Page<Product> list;
        List<Product> topProducts = productService.findTop6Product();
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

    @PostMapping("/add-to-cart")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpServletRequest request) {

        // Lấy accountId từ session
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "Vui lòng đăng nhập trước khi thêm sản phẩm vào giỏ hàng!";
        }

        // Kiểm tra sản phẩm có tồn tại hay không
        Product product = productService.findById(productId);
        if (product == null) {
            return "Sản phẩm không tồn tại!";
        }

        // Tìm Account từ cơ sở dữ liệu
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return "Tài khoản không hợp lệ!";
        }
        Account account = accountOptional.get();

        // Tính toán giá cuối cùng sau khi giảm giá (nếu có)
        BigDecimal finalPrice = product.getPrice();  // Bắt đầu với giá gốc
        if (product.getSale() != null && product.getSale().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = product.getPrice().subtract(product.getSale());  // Giảm giá nếu có
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Optional<Cart> existingCart = cartService.findByAccountAndProduct(account, product);
        if (existingCart.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);  // Cộng thêm số lượng
            cart.setPrice(finalPrice.doubleValue());  // Cập nhật giá
            cartService.save(cart);  // Lưu giỏ hàng đã cập nhật
            return "Sản phẩm đã được cập nhật trong giỏ hàng!";
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới giỏ hàng
            Cart cart = new Cart();
            cart.setAccount(account);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cart.setPrice(finalPrice.doubleValue());  // Sử dụng giá sau giảm giá nếu có
            cartService.save(cart);  // Lưu giỏ hàng mới
            return "Sản phẩm đã được thêm vào giỏ hàng!";
        }
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
        List<Product> topProducts = productService.findTop6Product();
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

    /**
     * Lấy accountId từ session.
     */
    private Integer getAccountIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object accountIdObj = session.getAttribute("accountId");

        // Kiểm tra và chuyển đổi kiểu dữ liệu từ String sang Integer
        if (accountIdObj instanceof String) {
            try {
                return Integer.parseInt((String) accountIdObj);
            } catch (NumberFormatException e) {
                // Nếu không thể chuyển đổi, trả về null
                return null;
            }
        }
        return (Integer) accountIdObj; // Nếu accountId là Integer, trả về trực tiếp
    }
}