package devmagic.Controller.User;



import devmagic.Model.Account;
import devmagic.Model.Cart;
import devmagic.Model.Product;
import devmagic.Service.AccountService;
import devmagic.Service.CartService;
import devmagic.Service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeUserController {
    @Autowired
    ProductService productService;
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

        // Lấy dữ liệu từ cookie (ví dụ lấy username từ cookie)
        String username = getUsernameFromCookies(request);
        Integer accountId = getAccountIdFromCookies(request);

        if (username != null) {
            model.addAttribute("username", username); // Thêm vào model để hiển thị trên trang
        }
        if (accountId != null) {
            model.addAttribute("accountId", accountId); // Thêm vào model để hiển thị trên trang
        }

        Page<Product> list;
        List<Product> product2 = this.productService.finTop6Product();
        Page<Product> list1 = this.productService.getForAll(pageNo);

        // Kiểm tra keyword (tìm kiếm) hoặc bộ lọc giá
        if (keyword != null) {
            list = this.productService.seachProduct(keyword, pageNo);
            model.addAttribute("keyword", keyword);
        } else if (minPrice != null || maxPrice != null) {
            list = this.productService.filterProductsByPrice(minPrice, maxPrice, pageNo);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        } else {
            list = this.productService.getall(pageNo); // Lấy toàn bộ sản phẩm nếu không có bộ lọc
        }

        model.addAttribute("totalPage", list.getTotalPages());
        model.addAttribute("currenPage", pageNo);
        model.addAttribute("product", list);
        model.addAttribute("product1", list1);
        model.addAttribute("product2", product2);

        return "layout/Home";
    }

    @PostMapping("/add-to-cart")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpServletRequest request) {

        // Lấy accountId từ cookie
        Integer accountId = getAccountIdFromCookies(request);
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


    // Phương thức để lấy username từ cookie
    private String getUsernameFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {  // Kiểm tra cookie tên 'username'
                    return cookie.getValue();  // Trả về giá trị của cookie 'username'
                }
            }
        }
        return null; // Nếu không có cookie 'username'
    }

    // Phương thức để lấy accountId từ cookie
    private Integer getAccountIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accountId".equals(cookie.getName())) {  // Kiểm tra cookie tên 'accountId'
                    try {
                        return Integer.parseInt(cookie.getValue());  // Chuyển giá trị cookie thành Integer
                    } catch (NumberFormatException e) {
                        // Nếu không thể chuyển đổi giá trị của cookie thành Integer, log lỗi
                        System.out.println("Lỗi khi chuyển đổi giá trị accountId từ cookie: " + e.getMessage());
                        return null;
                    }
                }
            }
        }
        return null; // Nếu không có cookie 'accountId'
    }


    @RequestMapping("/layout/Product")
    public String Product(Model model,@RequestParam("cid")Optional<String> cid) {
        if (cid.isPresent()) {
            List<Product> list = this.productService.findByCategoryId(cid.get());
            model.addAttribute("product", list);
        }else{
            List<Product> list = this.productService.findAll();
            model.addAttribute("product", list);
        }
        return "layout/Product";
    }



    @RequestMapping("/product/detail/{id}")
    public String ProductDetail(Model model , @PathVariable("id") Integer id) {
        Product item = productService.findById(id);
        List<Product> product2 = this.productService.finTop6Product();
        model.addAttribute("item", item);
        model.addAttribute("product2", product2);
        return "layout/ProductDetail";
    }

    @GetMapping("/user/login")
    public String login(Model model) {

        return "user/login";
    }

    @GetMapping("user/contact")
    public String contact(Model model) {
        return "user/contact";
    }

    @GetMapping("user/register")
    public String register(Model model) {
        return "user/register";
    }
    @GetMapping("ForgotPassword")
    public String ForgotPassword(Model model) {
        return "ForgotPassword";
    }
}

