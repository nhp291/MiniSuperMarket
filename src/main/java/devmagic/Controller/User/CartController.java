package devmagic.Controller.User;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.*;
import devmagic.Service.CartService;
import devmagic.Service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    @Autowired
    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    // Hiển thị giỏ hàng
    @GetMapping("/shoppingcart")
    public String shoppingCart(HttpServletRequest request, Model model) {
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login";  // Nếu không có accountId thì chuyển hướng đến trang login
        }
        // Lấy thông tin người dùng từ session
        String username = getAuthenticatedUsername();
        String role = getAuthenticatedRole();

        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("role", role);
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }

        // Lấy thông tin giỏ hàng
        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
        double totalPrice = cartService.calculateTotalPrice(cartItems);
        int totalQuantity = cartService.calculateTotalQuantity(cartItems);

        // Truyền dữ liệu vào model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);

        return "cart/shoppingcart";  // Trả về trang giỏ hàng
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PostMapping("/updateQuantity")
    @ResponseBody
    public String updateQuantity(@RequestParam("id") Integer cartId,
                                 @RequestParam("quantity") Integer quantity) {
        if (cartId == null || quantity == null || quantity <= 0) {
            return "error=invalid_quantity";  // Kiểm tra số lượng hợp lệ
        }

        Cart cart = cartService.findById(cartId);
        if (cart == null) {
            return "error=cart_not_found";  // Nếu không tìm thấy giỏ hàng
        }

        cart.setQuantity(quantity);  // Cập nhật số lượng sản phẩm
        cartService.save(cart);  // Lưu vào database
        return "success";  // Trả về thành công
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/removeItem")
    @ResponseBody
    public String removeItem(@RequestParam("id") Integer cartId) {
        if (cartId == null) {
            return "error=invalid_id";  // Kiểm tra id hợp lệ
        }

        Cart cart = cartService.findById(cartId);
        if (cart == null) {
            return "error=cart_not_found";  // Nếu không tìm thấy giỏ hàng
        }

        cartService.deleteById(cartId);  // Xóa sản phẩm khỏi database
        return "success";  // Trả về thành công
    }


    // Xử lý khi người dùng nhấn nút "Checkout"
    @PostMapping("/checkout")
    public String checkout(HttpServletRequest request, Model model,
                           @RequestParam("paymentMethod") String paymentMethod) {
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login";  // Nếu không có accountId thì chuyển hướng đến trang login
        }

        // Lấy thông tin giỏ hàng
        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(accountId);
        if (cartItems.isEmpty()) {
            return "redirect:/cart/shoppingcart";  // Nếu giỏ hàng trống thì chuyển hướng lại trang giỏ hàng
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setAccount(new Account(accountId));
        order.setOrderDate(new Date());
        order.setPaymentStatus("Pending");
        order.setPaymentMethod(paymentMethod); // Sử dụng phương thức thanh toán được chọn

        order.setOrderDetails(cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct()); // Sử dụng đối tượng Product từ CartItemDTO
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            return orderDetail;
        }).collect(Collectors.toList()));

        // Lưu đơn hàng và chi tiết đơn hàng vào database, đồng thời cập nhật số lượng sản phẩm trong kho
        orderService.createOrder(order);

        // Xóa giỏ hàng sau khi tạo đơn hàng
        cartService.clearCart(accountId);

        // Tính tổng giá trị đơn hàng
        double totalOrderPrice = order.getOrderDetails().stream()
                .mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity())
                .sum();

        // Truyền dữ liệu đơn hàng vào model để hiển thị trên trang invoice
        model.addAttribute("order", order);
        model.addAttribute("totalOrderPrice", totalOrderPrice);

        return "cart/thankyou";  // Trả về trang hóa đơn với đường dẫn chính xác
    }



    // Lấy accountId từ session
    private Integer getAccountIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object accountIdObj = session.getAttribute("accountId");

        // Kiểm tra nếu session không có accountId hoặc accountId không phải kiểu Integer hoặc String
        if (accountIdObj == null) {
            return null;  // Không có accountId trong session
        }

        try {
            // Nếu là Integer, trả về trực tiếp. Nếu là String, chuyển sang Integer.
            if (accountIdObj instanceof Integer) {
                return (Integer) accountIdObj;
            } else if (accountIdObj instanceof String) {
                return Integer.parseInt((String) accountIdObj);  // Chuyển từ String sang Integer
            }
        } catch (NumberFormatException e) {
            // Xử lý lỗi nếu không thể chuyển đổi
            return null;
        }
        return null;
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

    @GetMapping("/backToShop")
    public String backToShop(HttpServletRequest request) {
        Integer accountId = getAccountIdFromSession(request);
        if (accountId == null) {
            return "redirect:/user/login";  // Nếu chưa đăng nhập, chuyển hướng tới trang đăng nhập
        }
        return "redirect:/layout/Home";  // Điều hướng đến trang layout/home
    }
}
