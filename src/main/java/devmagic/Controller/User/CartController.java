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
import java.text.DecimalFormat;
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
        model.addAttribute("username", account.getUsername()); // Truyền đúng thông tin username
        model.addAttribute("phoneNumber", account.getPhoneNumber()); // Truyền thông tin phoneNumber
        model.addAttribute("address", account.getAddress()); // Truyền thông tin address
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

    // Tiến hành thanh toán
    @PostMapping("/checkout")
    public String checkout(HttpServletRequest request, Model model,
                           @RequestParam("paymentMethod") String paymentMethod,
                           @RequestParam(value = "note", required = false) String note) {

        // Lấy accountId từ session
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }

        // Lấy danh sách sản phẩm trong giỏ hàng
        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Giỏ hàng trống.");
            return "redirect:/cart/shoppingcart"; // Nếu giỏ hàng trống, quay lại trang giỏ hàng
        }

        // Cập nhật giá theo khuyến mãi cho từng sản phẩm trong giỏ hàng
        cartItems.forEach(this::updatePriceWithSale);

        // Tạo đơn hàng mới
        Order order = new Order();
        Optional<Account> accountOpt = cartService.getAccountById(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            order.setAccount(account);
        } else {
            model.addAttribute("error", "Không tìm thấy tài khoản người dùng.");
            return "redirect:/cart/shoppingcart";
        }

        order.setOrderDate(new Date()); // Ngày đặt hàng là ngày hiện tại
        order.setPaymentStatus("PENDING"); // Trạng thái thanh toán mặc định
        order.setPaymentMethod(paymentMethod); // Phương thức thanh toán
        order.setNote(note == null || note.isEmpty() ? null : note); // Ghi chú nếu có

        // Tạo chi tiết đơn hàng từ giỏ hàng
        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setOrderDetails(orderDetails);

        // Lưu đơn hàng vào cơ sở dữ liệu
        try {
            orderService.createOrder(order);
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi tạo đơn hàng.");
            return "redirect:/cart/shoppingcart";
        }

        // Xóa giỏ hàng sau khi đặt hàng thành công
        cartService.clearCart(accountId);

        // Tính tổng giá trị đơn hàng
        BigDecimal totalOrderPrice = orderDetails.stream()
                .map(orderDetail -> orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kiểm tra email người dùng để gửi xác nhận đơn hàng
        if (accountOpt.get().getEmail() == null || accountOpt.get().getEmail().isEmpty()) {
            model.addAttribute("error", "Email của tài khoản không hợp lệ.");
            return "redirect:/cart/shoppingcart";
        }

        String email = accountOpt.get().getEmail();

        // Gửi email xác nhận đơn hàng
        String emailContent = generateOrderEmailContent(order, totalOrderPrice, cartItems);
        try {
            emailService.sendOrderConfirmationEmail(email, emailContent);
        } catch (MessagingException e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi gửi email xác nhận đơn hàng.");
            return "redirect:/cart/shoppingcart";
        }

        // Truyền thông tin đơn hàng đến view
        model.addAttribute("order", order);
        model.addAttribute("totalOrderPrice", totalOrderPrice);

        // Chuyển hướng đến trang cảm ơn
        return "cart/thankyou";
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
        // Định dạng số tiền kiểu Việt Nam: nhóm hàng nghìn, không có phần thập phân
        DecimalFormat currencyFormat = new DecimalFormat("#,###");

        // Sử dụng StringBuilder để xây dựng nội dung email
        StringBuilder emailContent = new StringBuilder();

        // Lấy thông tin tài khoản từ đơn hàng
        Account account = order.getAccount();
        String username = account != null && account.getUsername() != null ? account.getUsername() : "Khách hàng";
        String address = account != null && account.getAddress() != null ? account.getAddress() : "[Chưa cung cấp]";
        String phoneNumber = account != null && account.getPhoneNumber() != null ? account.getPhoneNumber() : "[Chưa cung cấp]";

        // Tiêu đề và lời chào
        emailContent.append("<html><body>")
                .append("<h1>Cảm ơn bạn đã đặt hàng tại DevMagic</h1>")
                .append("<p>Xin chào ").append(username).append(",</p>")
                .append("<p>Đơn hàng của bạn đã được xác nhận và đang trong quá trình xử lý. Dưới đây là thông tin chi tiết của đơn hàng:</p>");

        // Thông tin đơn hàng
        emailContent.append("<h3>Thông tin đơn hàng:</h3>")
                .append("<p><strong>Mã đơn hàng:</strong> ").append(order.getOrderId()).append("</p>")
                .append("<p><strong>Ngày đặt:</strong> ").append(order.getOrderDate()).append("</p>")
                .append("<p><strong>Phương thức thanh toán:</strong> ").append(order.getPaymentMethod()).append("</p>");

        // Thông tin địa chỉ giao hàng
        emailContent.append("<h3>Thông tin địa chỉ giao hàng:</h3>")
                .append("<p><strong>Họ và tên:</strong> ").append(username).append("</p>")
                .append("<p><strong>Địa chỉ:</strong> ").append(address).append("</p>")
                .append("<p><strong>Số điện thoại:</strong> ").append(phoneNumber).append("</p>");

        // Thêm ghi chú nếu có
        if (order.getNote() != null && !order.getNote().isEmpty()) {
            emailContent.append("<p><strong>Ghi chú:</strong> ").append(order.getNote()).append("</p>");
        }

        // Chi tiết sản phẩm
        emailContent.append("<h3>Chi tiết sản phẩm:</h3>")
                .append("<table border=\"1\" cellpadding=\"5\"><thead><tr>")
                .append("<th>Sản phẩm</th><th>Số lượng</th><th>Đơn giá</th><th>Tổng giá</th>")
                .append("</tr></thead><tbody>");

        // Lặp qua danh sách sản phẩm trong giỏ hàng
        for (CartItemDTO cartItem : cartItems) {
            BigDecimal effectivePrice = cartItem.getPrice(); // Đơn giá từ dữ liệu
            BigDecimal scaledEffectivePrice = effectivePrice.multiply(BigDecimal.valueOf(1000)); // Chuyển thành nghìn VNĐ
            BigDecimal totalPrice = scaledEffectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())); // Tổng giá

            emailContent.append("<tr>")
                    .append("<td>").append(cartItem.getProductName()).append("</td>")
                    .append("<td>").append(cartItem.getQuantity()).append("</td>")
                    .append("<td>").append(currencyFormat.format(scaledEffectivePrice)).append(" đồng</td>")
                    .append("<td>").append(currencyFormat.format(totalPrice)).append(" đồng</td>")
                    .append("</tr>");
        }

        // Kết thúc bảng chi tiết sản phẩm
        emailContent.append("</tbody></table>");

        // Tổng tiền đơn hàng
        BigDecimal scaledTotalOrderPrice = totalOrderPrice.multiply(BigDecimal.valueOf(1000)); // Chuyển thành nghìn VNĐ
        emailContent.append("<h3>Tổng tiền:</h3>")
                .append("<p><strong>Tổng giá trị đơn hàng:</strong> ")
                .append(currencyFormat.format(scaledTotalOrderPrice)).append(" đồng</p>");

        // Thông báo kết thúc
        emailContent.append("<p>Chúng tôi sẽ thông báo cho bạn khi đơn hàng của bạn được xử lý và giao hàng. Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>")
                .append("<p>Trân trọng,</p>")
                .append("<p><strong>DevMagic</strong></p>")
                .append("</body></html>");

        return emailContent.toString();
    }

    @GetMapping("/spinningwheel")
    public String spinningwheel(){
        return "cart/spinningwheel";
    }

}