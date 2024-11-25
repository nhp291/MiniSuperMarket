package devmagic.Controller.User;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.Cart;
import devmagic.Service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Hiển thị giỏ hàng
    @GetMapping("/shoppingcart")
    public String shoppingCart(HttpServletRequest request, Model model) {
        Integer userId = getUserIdFromSession(request);
        if (userId == null) {
            return "redirect:/user/login";  // Nếu không có userId thì chuyển hướng đến trang login
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


        // Lấy thông tin giỏ hàng
        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(userId);
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

    // Lấy userId từ session
    private Integer getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object accountIdObj = session.getAttribute("accountId");

        // Kiểm tra nếu session không có accountId hoặc accountId không phải kiểu Integer
        if (accountIdObj == null) {
            return null;  // Không có userId trong session
        }

        try {
            // Nếu là Integer, trả về. Nếu là String, chuyển sang Integer.
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