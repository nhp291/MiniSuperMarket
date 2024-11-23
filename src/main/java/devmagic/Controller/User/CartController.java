package devmagic.Controller.User;

import devmagic.Model.Cart;
import devmagic.Service.CartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Hàm tiện ích để lấy userId từ cookie
    private Integer getUserIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accountId".equals(cookie.getName())) {
                    try {
                        return Integer.parseInt(cookie.getValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    // Form 1: Quản lý giỏ hàng - chỉ tải form lên
    @GetMapping("/shoppingcart")
    public String shoppingCart(HttpServletRequest request, Model model) {
        Integer userId = getUserIdFromCookies(request);
        if (userId == null) {
            return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập nếu không có userId trong cookie
        }

        // Không tải dữ liệu thực tế, chỉ hiển thị trang giỏ hàng
        return "cart/shoppingcart";
    }

    // Form 2: Tạo hóa đơn - chỉ tải form lên
    @GetMapping("/invoice")
    public String invoice(HttpServletRequest request, Model model) {
        Integer userId = getUserIdFromCookies(request);
        if (userId == null) {
            return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập nếu không có userId trong cookie
        }

        // Không tải dữ liệu thực tế, chỉ hiển thị trang hóa đơn
        return "cart/invoice";
    }
}