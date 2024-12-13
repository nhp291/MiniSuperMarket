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

        // Lưu thông tin người dùng vào session nếu chưa có
        HttpSession session = request.getSession();
        if (session.getAttribute("accountId") == null) { // Kiểm tra theo accountId để tránh trùng
            Optional<Account> accountOpt = cartService.getAccountById(accountId);
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                session.setAttribute("accountId", account.getAccountId());
                session.setAttribute("username", account.getUsername());
                session.setAttribute("role", account.getRole().getRoleName());
                session.setAttribute("email", account.getEmail());
                session.setAttribute("phoneNumber", account.getPhoneNumber());
                session.setAttribute("address", account.getAddress());
            } else {
                model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
                return "redirect:/user/login";
            }
        }

        // Lấy thông tin tài khoản từ session
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("email");
        String phoneNumber = (String) session.getAttribute("phoneNumber");
        String address = (String) session.getAttribute("address");

        // Kiểm tra nếu phoneNumber và address là null, có thể gán giá trị mặc định
        if (phoneNumber == null) {
            phoneNumber = "Số điện thoại không có sẵn"; // Hoặc gán giá trị mặc định khác
        }
        if (address == null) {
            address = "Địa chỉ không có sẵn"; // Hoặc gán giá trị mặc định khác
        }

        // Thêm vào model để truyền thông tin ra view
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("address", address);
        model.addAttribute("email", email);

        System.out.println("Session phoneNumber: " + session.getAttribute("phoneNumber"));
        System.out.println("Session address: " + session.getAttribute("address"));


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

        Integer accountId = getAccountIdFromSession(request);  // Lấy accountId từ session
        if (accountId == null) {
            return ResponseEntity.status(401).build(); // Không có quyền
        }

        // Thực hiện cập nhật giỏ hàng cho người dùng
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

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email == null) {
            System.out.println("Email không tồn tại trong session.");
            model.addAttribute("emailError", "Không tìm thấy email trong session. Vui lòng đăng nhập lại.");
            return "redirect:/user/login";
        }

        // Gửi email thông báo đơn hàng
        try {
            String emailContent = generateOrderEmailContent(order, totalOrderPrice, cartItems);
            emailService.sendEmail(email, "Đặt hàng thành công DevMagic", emailContent);
            System.out.println("Email đã được gửi thành công cho người dùng: " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Có lỗi xảy ra khi gửi email cho người dùng: " + email);
            model.addAttribute("emailError", "Có lỗi xảy ra khi gửi email thông báo.");
        }

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

    private String generateOrderEmailContent(Order order, BigDecimal totalOrderPrice, List<CartItemDTO> cartItems) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<html><body>")
                .append("<h1>Cảm ơn bạn đã đặt hàng tại DevMagic</h1>")
                .append("<p>Xin chào ").append(order.getAccount().getUsername()).append(",</p>")
                .append("<p>Đơn hàng của bạn đã được xác nhận và đang trong quá trình xử lý. Dưới đây là thông tin chi tiết của đơn hàng:</p>")
                .append("<h3>Thông tin đơn hàng:</h3>")
                .append("<p><strong>Mã đơn hàng:</strong> ").append(order.getOrderId()).append("</p>")
                .append("<p><strong>Ngày đặt:</strong> ").append(order.getOrderDate()).append("</p>")
                .append("<p><strong>Phương thức thanh toán:</strong> ").append(order.getPaymentMethod()).append("</p>");

        // Thêm thông tin địa chỉ vào email
        Account account = order.getAccount();
        emailContent.append("<h3>Thông tin địa chỉ giao hàng:</h3>")
                .append("<p><strong>Địa chỉ:</strong> ").append(account.getAddress()).append("</p>")
                .append("<p><strong>Số điện thoại:</strong> ").append(account.getPhoneNumber()).append("</p>");

        if (order.getNote() != null && !order.getNote().isEmpty()) {
            emailContent.append("<p><strong>Ghi chú:</strong> ").append(order.getNote()).append("</p>");
        }

        emailContent.append("<h3>Chi tiết sản phẩm:</h3>")
                .append("<table border=\"1\" cellpadding=\"5\"><thead><tr><th>Sản phẩm</th><th>Số lượng</th><th>Đơn giá</th><th>Tổng giá</th></tr></thead><tbody>");

        // Thêm thông tin sản phẩm vào email
        for (CartItemDTO cartItem : cartItems) {
            emailContent.append("<tr>")
                    .append("<td>").append(cartItem.getProductName()).append("</td>")
                    .append("<td>").append(cartItem.getQuantity()).append("</td>")
                    .append("<td>").append(cartItem.getPrice()).append("</td>")
                    .append("<td>").append(cartItem.getTotalPrice()).append("</td>")
                    .append("</tr>");
        }

        emailContent.append("</tbody></table>")
                .append("<h3>Tổng tiền:</h3>")
                .append("<p><strong>Tổng giá trị đơn hàng:</strong> ").append(totalOrderPrice).append("</p>")
                .append("<p>Chúng tôi sẽ thông báo cho bạn khi đơn hàng của bạn được xử lý và giao hàng. Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>")
                .append("<p>Trân trọng,</p><p><strong>DevMagic</strong></p>")
                .append("</body></html>");

        return emailContent.toString();
    }

}
