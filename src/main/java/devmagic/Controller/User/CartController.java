package devmagic.Controller.User;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.Cart;
import devmagic.Service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
        Integer userId = getUserIdFromCookies(request);
        if (userId == null) {
            return "redirect:/user/login";
        }

        // Lấy thông tin giỏ hàng
        List<CartItemDTO> cartItems = cartService.getCartItemDTOs(userId);
        double totalPrice = cartService.calculateTotalPrice(cartItems);
        int totalQuantity = cartService.calculateTotalQuantity(cartItems);

        // Truyền dữ liệu vào model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);

        return "cart/shoppingcart";
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PostMapping("/updateQuantity")
    @ResponseBody
    public String updateQuantity(@RequestParam("id") Integer cartId,
                                 @RequestParam("quantity") Integer quantity) {
        if (cartId == null || quantity == null || quantity <= 0) {
            return "error=invalid_quantity";
        }

        Cart cart = cartService.findById(cartId);
        if (cart == null) {
            return "error=cart_not_found";
        }

        cart.setQuantity(quantity);  // Cập nhật số lượng sản phẩm
        cartService.save(cart);  // Lưu vào database
        return "success";
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/removeItem")
    @ResponseBody
    public String removeItem(@RequestParam("id") Integer cartId) {
        if (cartId == null) {
            return "error=invalid_id";
        }

        Cart cart = cartService.findById(cartId);
        if (cart == null) {
            return "error=cart_not_found";
        }

        cartService.deleteById(cartId);  // Xóa sản phẩm khỏi database
        return "success";
    }

    // Lấy userId từ cookie
    private Integer getUserIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accountId".equals(cookie.getName())) {
                    try {
                        return Integer.parseInt(cookie.getValue().trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
