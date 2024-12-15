package devmagic.Controller.User;

import devmagic.Message.ResponseMessage;
import devmagic.Model.Account;
import devmagic.Model.Cart;
import devmagic.Model.Product;
import devmagic.Model.Role;
import devmagic.Reponsitory.RoleRepository;
import devmagic.Service.AccountService;
import devmagic.Service.CartService;
import devmagic.Service.EmailService;
import devmagic.Service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
public class HomeUserController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CartService cartService;

    @Autowired
    private RoleRepository roleRepository;

    private static final Map<String, String> verificationCodes = new HashMap<>();

    @RequestMapping("/layout/Home")
    public String Home(
            Model model,
            @Param("keyword") String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "categoryId", required = false) Integer categoryId, // Thêm tham số phân loại
            HttpServletRequest request) {
        // Lấy thông tin người dùng từ session
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();
        Integer accountId = getAccountIdFromSession(request);

        // Lấy số lượng sản phẩm trong giỏ hàng
        int totalQuantity = cartService.calculateTotalQuantity(cartService.getCartItemDTOs(accountId));
        model.addAttribute("totalQuantity", totalQuantity);

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

        // Xử lý tìm kiếm hoặc lọc theo giá hoặc phân loại theo danh mục
        if (keyword != null) {
            list = productService.seachProductt(keyword, pageNo);
            model.addAttribute("keyword", keyword);
            if (list.getContent().isEmpty()) {
                model.addAttribute("noProductsMessage", "Không tìm thấy sản phẩm nào với từ khóa: " + keyword);
            }
        }    else if (minPrice != null || maxPrice != null) {
            // Nếu có lọc theo giá và có categoryId thì lọc theo cả hai
            if (categoryId != null) {
                list = productService.filterProductsByPriceAndCategory(minPrice, maxPrice, categoryId, pageNo);
            } else {
                // Nếu chỉ có lọc theo giá
                list = productService.filterProductsByPrice(minPrice, maxPrice, pageNo);
            }
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        } else if (categoryId != null) {
            // Nếu chỉ có lọc theo danh mục
            list = productService.filterProductsByCategory(categoryId, pageNo);
            model.addAttribute("categoryId", categoryId);
        } else {
            // Nếu không có bộ lọc, lấy tất cả sản phẩm
            list = productService.getall(pageNo);
        }

        model.addAttribute("totalPage", list.getTotalPages());
        model.addAttribute("currenPage", pageNo);
        model.addAttribute("product", list);
        model.addAttribute("product1", paginatedProducts);
        model.addAttribute("product2", topProducts);

        return "layout/Home";
    }

    @GetMapping("/cart/getTotalQuantity")
    @ResponseBody
    public Map<String, Integer> getTotalQuantity(HttpServletRequest request) {
        // Lấy accountId từ session
        Integer accountId = getAccountIdFromSession(request);

        Map<String, Integer> response = new HashMap<>();
        if (accountId == null) {
            response.put("totalQuantity", 0); // Nếu không tìm thấy accountId, trả về 0
            return response;
        }

        // Tính tổng số lượng sản phẩm trong giỏ hàng
        int totalQuantity = cartService.calculateTotalQuantity(cartService.getCartItemDTOs(accountId));
        response.put("totalQuantity", totalQuantity);
        return response;
    }

    @PostMapping("/cart/shoppingcart")
    @ResponseBody
    public ResponseEntity<?> addaToCart(@RequestParam("productId") Integer productId,
                                        @RequestParam("quantity") Integer quantity,
                                        HttpServletRequest request) {

        // Lấy accountId từ session
        Integer accountId = getAccountIdFromSession(request);
        // Kiểm tra sản phẩm có tồn tại hay không
        Product product = productService.findById(productId);
        if (product == null) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Sản phẩm không tồn tại!"));
        }

        // Tìm Account từ cơ sở dữ liệu
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Tài khoản không hợp lệ!"));
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
            cart.setPrice(BigDecimal.valueOf(finalPrice.doubleValue()));  // Cập nhật giá
            cartService.save(cart);  // Lưu giỏ hàng đã cập nhật
            return ResponseEntity.ok(new ResponseMessage("Sản phẩm đã được cập nhật trong giỏ hàng!"));
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới giỏ hàng
            Cart cart = new Cart();
            cart.setAccount(account);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cart.setPrice(BigDecimal.valueOf(finalPrice.doubleValue()));  // Sử dụng giá sau giảm giá nếu có
            cartService.save(cart);  // Lưu giỏ hàng mới
            // Trả về thông báo thành công
            return ResponseEntity.ok(new ResponseMessage("Sản phẩm đã được thêm vào giỏ hàng!"));
        }

    }


    @RequestMapping("/layout/Product")
    public String Product(Model model, @RequestParam("cid") Optional<String> cid, HttpServletRequest request) {
        // Lấy thông tin người dùng từ session
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();
        Integer accountId = getAccountIdFromSession(request);
        // Lấy số lượng sản phẩm trong giỏ hàng
        int totalQuantity = cartService.calculateTotalQuantity(cartService.getCartItemDTOs(accountId));

        // Truyền số lượng vào model để sử dụng trong template
        model.addAttribute("totalQuantity", totalQuantity);
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }
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
    public String ProductDetail(Model model, @PathVariable(value = "id", required = false) Integer id, HttpServletRequest request) {
        // Kiểm tra giá trị id
        if (id == null) {
            model.addAttribute("error", "ID sản phẩm không hợp lệ hoặc không được cung cấp.");
            return "errorPage"; // Trả về trang lỗi (tạo một trang lỗi phù hợp nếu chưa có)
        }

        // Lấy thông tin người dùng từ session
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();
        Integer accountId = getAccountIdFromSession(request);
        // Lấy số lượng sản phẩm trong giỏ hàng
        int totalQuantity = cartService.calculateTotalQuantity(cartService.getCartItemDTOs(accountId));

        // Truyền số lượng vào model để sử dụng trong template
        model.addAttribute("totalQuantity", totalQuantity);
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }

        // Tìm sản phẩm theo ID
        Product item = productService.findById(id);
        if (item == null) {
            model.addAttribute("error", "Sản phẩm không tồn tại.");
            return "errorPage"; // Trả về trang lỗi nếu sản phẩm không được tìm thấy
        }

        // Lấy danh sách các sản phẩm hàng đầu
        List<Product> topProducts = productService.findTop6Product();

        // Thêm thông tin vào model để hiển thị trên giao diện
        model.addAttribute("item", item);
        model.addAttribute("product2", topProducts);
        return "layout/ProductDetail";
    }


    @GetMapping("/user/login")
    public String login(Model model) {
        return "user/login";
    }

    @GetMapping("/user/contact")
    public String contact(Model model, HttpServletRequest request) {
        // Lấy thông tin người dùng từ session
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();
        Integer accountId = getAccountIdFromSession(request);
        // Lấy số lượng sản phẩm trong giỏ hàng
        int totalQuantity = cartService.calculateTotalQuantity(cartService.getCartItemDTOs(accountId));

        // Truyền số lượng vào model để sử dụng trong template
        model.addAttribute("totalQuantity", totalQuantity);
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }
        return "user/contact";
    }

    @PostMapping("/user/checkEmailExists")
    @ResponseBody
    public ResponseEntity<?> checkEmailExists(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/sendVerificationCode")
    @ResponseBody
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Email không được để trống!"));
        }

        // Sinh mã xác nhận ngẫu nhiên
        String verificationCode = String.valueOf(new Random().nextInt(999999));
        verificationCodes.put(email, verificationCode);

        // Gửi mã qua email
        try {
            emailService.sendEmail(email, "Mã xác nhận của bạn", "Mã xác nhận: " + verificationCode);
            return ResponseEntity.ok(new ResponseMessage("Mã xác nhận đã được gửi."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseMessage("Không thể gửi mã xác nhận."));
        }
    }

    @PostMapping("/user/verifyCode")
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code)) {
            verificationCodes.remove(email); // Xóa mã sau khi xác nhận
            return ResponseEntity.ok(new ResponseMessage("Mã xác nhận chính xác!"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Mã xác nhận không hợp lệ."));
        }
    }

    @GetMapping("user/register")
    public String registerForm(Model model) {
        model.addAttribute("account", new Account()); // Đảm bảo tạo một đối tượng Account mới
        return "user/register"; // Trả về trang đăng ký
    }

    @PostMapping("user/register")
    public String register(@Valid @ModelAttribute("account") Account account,
                           @RequestParam("verificationCode") String verificationCode,
                           BindingResult result,
                           Model model) throws IOException {
        String email = account.getEmail();

        // Kiểm tra mã xác nhận
        if (!verificationCodes.containsKey(email) ||
                !verificationCodes.get(email).equals(verificationCode)) {
            model.addAttribute("verificationError", "Mã xác nhận không chính xác!");
        }

        // Kiểm tra email đã tồn tại chưa
        if (accountService.emailExists(account.getEmail())) {
            result.rejectValue("email", "error.email", "Email đã được sử dụng!");
        }

        // Kiểm tra mật khẩu có khớp không
        if (!account.getPassword().equals(account.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp!");
        }

        // Kiểm tra định dạng email
        if (!account.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.rejectValue("email", "error.email", "Email không hợp lệ!");
        }

        // Kiểm tra số điện thoại
        if (!account.getPhoneNumber().matches("^[0-9]*$")) {
            result.rejectValue("phoneNumber", "error.phoneNumber", "Số điện thoại chỉ chứa chữ số!");
        }

        // Kiểm tra lỗi nhập liệu hoặc lỗi mã xác nhận
        if (result.hasErrors() || model.containsAttribute("verificationError")) {
            model.addAttribute("account", account);
            model.addAttribute("error", "Có lỗi xảy ra. Vui lòng kiểm tra lại thông tin.");
            return "user/register";
        }

        // Gán vai trò mặc định USER (ID = 2)
        Role defaultRole = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Role mặc định không tồn tại."));
        account.setRole(defaultRole);

        // Gán ảnh đại diện mặc định
        String defaultImage = "User.png"; // Nếu không upload ảnh, dùng ảnh mặc định
        account.setImageUrl(defaultImage);

        // Lưu thông tin tài khoản
        try {
            accountService.saveAccount(account);
            model.addAttribute("successMessage", "Đăng ký thành công! Bạn sẽ được chuyển hướng đến trang đăng nhập sau 3 giây.");
            return "redirect:/user/register?success=true"; // Trả về tham số success
        } catch (Exception e) {
            model.addAttribute("account", account);
            model.addAttribute("error", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.");
            return "user/register";
        }
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


    //Lấy accountId từ session.
    private Integer getAccountIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Chỉ lấy session nếu tồn tại
        if (session == null) {
            return null;
        }
        Object accountIdObj = session.getAttribute("accountId");
        if (accountIdObj instanceof Integer) {
            return (Integer) accountIdObj;
        } else if (accountIdObj instanceof String) {
            try {
                return Integer.parseInt((String) accountIdObj);
            } catch (NumberFormatException e) {
                return null; // Trả về null nếu không thể chuyển đổi
            }
        }
        return null;
    }
}