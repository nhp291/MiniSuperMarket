package devmagic.Controller.User;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.*;
import devmagic.Reponsitory.AccountRepository;
import devmagic.Reponsitory.CartRepository;
import devmagic.Service.CartService;
import devmagic.Service.OrderService;
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
    private final AccountRepository accountRepository;

    @Autowired
    public CartController(CartService cartService, OrderService orderService, CartRepository cartRepository, AccountRepository accountRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.cartRepository = cartRepository;
        this.accountRepository = accountRepository;
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
        Optional<Account> accountOpt = accountRepository.findById(accountId);
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

    // Tiến hành thanh toán
    @PostMapping("/checkout")
    public String checkout(HttpServletRequest request, Model model,
                           @RequestParam("paymentMethod") String paymentMethod,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "note", required = false) String note) {
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login";
        }

        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return "redirect:/user/login";
        }
        Account account = accountOpt.get();

        // Cập nhật email nếu người dùng thay đổi
        if (email != null && !email.equals(account.getEmail())) {
            account.setEmail(email);
            accountRepository.save(account);
        }

        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng trống.");
            return "redirect:/cart/shoppingcart";
        }

        cartItems.forEach(this::updatePriceWithSale);

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setAccount(account);
        order.setOrderDate(new Date());
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(paymentMethod);

        // Tạo chi tiết đơn hàng
        order.setOrderDetails(cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            return orderDetail;
        }).collect(Collectors.toList()));

        // Lưu đơn hàng
        orderService.createOrder(order);

        // Lưu ghi chú nếu có
        if (note != null && !note.isBlank()) {
            List<Cart> carts = cartRepository.findByAccount_AccountId(accountId);
            for (Cart cart : carts) {
                cart.setNote(note);
                cartRepository.save(cart);
            }
        }

        // Xóa giỏ hàng sau khi đặt hàng thành công
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
}
