package devmagic.Controller.User;

import devmagic.Message.ResponseMessage;
import devmagic.Model.Account;
import devmagic.Model.Cart;
import devmagic.Model.Product;
import devmagic.Model.Role;
import devmagic.Reponsitory.RoleRepository;
import devmagic.Service.AccountService;
import devmagic.Service.CartService;
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

    @Autowired
    private RoleRepository roleRepository;

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
            cart.setPrice(finalPrice.doubleValue());  // Cập nhật giá
            cartService.save(cart);  // Lưu giỏ hàng đã cập nhật
            return ResponseEntity.ok(new ResponseMessage("Sản phẩm đã được cập nhật trong giỏ hàng!"));
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới giỏ hàng
            Cart cart = new Cart();
            cart.setAccount(account);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cart.setPrice(finalPrice.doubleValue());  // Sử dụng giá sau giảm giá nếu có
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

        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }
        return "user/contact";
    }

    @GetMapping("user/register")
    public String registerForm(Model model) {
        model.addAttribute("account", new Account());
        return "user/register"; // Trả về trang đăng ký
    }

    @PostMapping("user/register")
    public String register(@Valid @ModelAttribute("account") Account account,
                           BindingResult result,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes,
                           Model model) throws IOException {

        // Kiểm tra lỗi nhập liệu
        if (validateAccount(account, result)) {
            return "user/register";
        }

        // Gán vai trò mặc định USER (ID = 2)
        Role defaultRole = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Role mặc định không tồn tại."));
        account.setRole(defaultRole);

        // Xử lý ảnh đại diện
        String defaultImage = "User.png"; // Nếu không upload ảnh, dùng ảnh mặc định
        account.setImageUrl(defaultImage);
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uploadDir = "src/main/resources/static/Image/imageProfile/";
            Path uploadPath = Paths.get(uploadDir);

            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file ảnh vào thư mục
            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                account.setImageUrl(fileName); // Lưu tên ảnh vào database
            } catch (IOException e) {
                model.addAttribute("error", "Không thể lưu tệp hình ảnh.");
                return "user/register";
            }
        }

        // Lưu thông tin tài khoản
        try {
            accountService.saveAccount(account);
            return "redirect:/user/register?success=true"; // Chuyển hướng với tham số success
        } catch (Exception e) {
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

    private boolean validateAccount (Account account, BindingResult result){
            boolean hasErrors = false;

            if (accountService.isUsernameExist(account.getUsername())) {
                result.rejectValue("username", "error.account", "Tên đăng nhập đã tồn tại.");
                hasErrors = true;
            }

            if (accountService.isEmailExist(account.getEmail())) {
                result.rejectValue("email", "error.account", "Email đã tồn tại.");
                hasErrors = true;
            }

            if (!account.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$")) {
                result.rejectValue("password", "error.account", "Mật khẩu phải có ít nhất 6 ký tự, gồm chữ thường, chữ hoa và ký tự đặc biệt.");
                hasErrors = true;
            }

            if (account.getPhoneNumber() == null || !account.getPhoneNumber().matches("^\\d{10,15}$")) {
                result.rejectValue("phoneNumber", "error.account", "Số điện thoại phải là số, từ 10 đến 15 ký tự.");
                hasErrors = true;
            }

            return hasErrors;

        }
    }