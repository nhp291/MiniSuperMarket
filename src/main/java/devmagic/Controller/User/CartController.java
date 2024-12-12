package devmagic.Controller.User;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.*;
import devmagic.Reponsitory.CartRepository;
import devmagic.Service.CartService;
import devmagic.Service.EmailService;
import devmagic.Service.OrderService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final CartRepository cartRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    public CartController(CartService cartService, OrderService orderService, CartRepository cartRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.cartRepository = cartRepository;
    }

    // Hiển thị giỏ hàng
    @GetMapping("/shoppingcart")
    public String shoppingCart(HttpServletRequest request, Model model) {
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login";
        }

        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();

        // Lấy thông tin tài khoản từ database
        Optional<Account> accountOpt = cartService.getAccountById(accountId);
        if (accountOpt.isEmpty()) {
            return "redirect:/user/login";  // Nếu không tìm thấy tài khoản, chuyển hướng đến trang đăng nhập
        }
        Account account = accountOpt.get();

        // Thêm thông tin tài khoản vào model để hiển thị
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("accountId", accountId);
        model.addAttribute("account", account);  // Truyền đối tượng Account vào model

        // Lấy các sản phẩm trong giỏ hàng của người dùng
        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
        cartItems.forEach(this::updatePriceWithSale);

        // Tính tổng giá trị và số lượng
        BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
        int totalQuantity = cartService.calculateTotalQuantity(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);

        return "cart/shoppingcart";  // Trả về view giỏ hàng
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PostMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, Integer> body, HttpServletRequest request) {
        Integer productId = body.get("productId");
        Integer quantity = body.get("quantity");

        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return ResponseEntity.status(401).build(); // Không có quyền
        }

        Optional<Cart> cartOpt = cartRepository.findByAccount_AccountIdAndProduct_ProductId(accountId, productId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            Product product = cart.getProduct();

            // Kiểm tra số lượng tồn kho và điều kiện hợp lệ
            if (quantity > 0 && quantity <= product.getStockQuantity()) {
                cart.setQuantity(quantity);
                cartRepository.save(cart);

                // Tính toán giá mới và tổng tiền
                List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
                cartItems.forEach(this::updatePriceWithSale);

                BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
                int totalQuantity = cartService.calculateTotalQuantity(cartItems);

                BigDecimal newPrice = calculateEffectivePrice(product).multiply(BigDecimal.valueOf(quantity));

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "newPrice", newPrice,
                        "totalPrice", totalPrice,
                        "totalQuantity", totalQuantity
                ));
            }
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/remove-item")
    public ResponseEntity<?> removeItem(@RequestBody Map<String, Integer> body, HttpServletRequest request) {
        Integer productId = body.get("productId");

        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return ResponseEntity.status(401).build(); // Không có quyền
        }

        Optional<Cart> cartOpt = cartRepository.findByAccount_AccountIdAndProduct_ProductId(accountId, productId);
        if (cartOpt.isPresent()) {
            cartRepository.delete(cartOpt.get());

            // Tính toán lại tổng tiền và tổng số lượng
            List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
            cartItems.forEach(this::updatePriceWithSale);

            BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
            int totalQuantity = cartService.calculateTotalQuantity(cartItems);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalPrice", totalPrice,
                    "totalQuantity", totalQuantity
            ));
        }
        return ResponseEntity.ok(Map.of("success", false));
    }

    // Tiến hành thanh toán

    @PostMapping("/checkout")
    public String checkout(HttpServletRequest request, Model model,
                           @RequestParam("paymentMethod") String paymentMethod,
                           @RequestParam(value = "note", required = false) String note) {

        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login";
        }

        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng trống.");
            return "redirect:/cart/shoppingcart";
        }

        cartItems.forEach(this::updatePriceWithSale);

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setAccount(new Account(accountId));
        order.setOrderDate(new Date());
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(paymentMethod);

        // Set note vào đơn hàng (null nếu không có ghi chú)
        order.setNote(note == null || note.isEmpty() ? null : note);

        // Tạo chi tiết đơn hàng
        order.setOrderDetails(cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            return orderDetail;
        }).collect(Collectors.toList()));

        // Lưu đơn hàng vào cơ sở dữ liệu
        orderService.createOrder(order);
        cartService.clearCart(accountId);

        // Tính tổng giá trị đơn hàng
        BigDecimal totalOrderPrice = order.getOrderDetails().stream()
                .map(orderDetail -> orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("order", order);
        model.addAttribute("totalOrderPrice", totalOrderPrice);

        return "cart/thankyou";  // Trang cảm ơn sau khi thanh toán thành công
    }

    // Helper: Cập nhật giá sản phẩm với giá sale (nếu có)
    private void updatePriceWithSale(CartItemDTO cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal effectivePrice = calculateEffectivePrice(product);
        cartItem.setPrice(effectivePrice);
        cartItem.calculateTotalPrice();
    }

    // Helper: Tính giá hiệu quả (sale hoặc giá gốc)
    private BigDecimal calculateEffectivePrice(Product product) {
        return (product.getSale() != null && product.getSale().compareTo(BigDecimal.ZERO) > 0)
                ? product.getSale()
                : product.getPrice();
    }

    private Integer getAccountIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object accountIdObj = session.getAttribute("accountId");

        if (accountIdObj == null) {
            return null;
        }

        try {
            if (accountIdObj instanceof Integer) {
                return (Integer) accountIdObj;
            } else if (accountIdObj instanceof String) {
                return Integer.parseInt((String) accountIdObj);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }

    private String getAuthenticatedRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @PostMapping("/save-gift")
    public ResponseEntity<?> saveGiftToSession(@RequestBody Map<String, String> body, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String giftCode = body.get("giftCode");
        session.setAttribute("giftCode", giftCode);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
