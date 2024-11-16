package devmagic.Controller.User;

import devmagic.Service.CartService;
import devmagic.Dto.CartDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

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

    @GetMapping("/shoppingcart")
    public String shoppingCart(HttpServletRequest request, Model model) {
        Integer userId = getUserIdFromCookies(request);
        if (userId == null) {
            return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập nếu không có userId trong cookie
        }

        CartDto cartDto = cartService.getCartByUserId(userId);
        double totalPrice = cartService.calculateTotalPrice(cartDto);
        model.addAttribute("cart", cartDto);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/shoppingcart"; // trả về view shoppingcart.html
    }

    @PostMapping("/updateQuantity/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(@PathVariable Integer productId, @RequestBody Map<String, Integer> request, HttpServletRequest requestHttp) {
        Integer userId = getUserIdFromCookies(requestHttp);
        if (userId == null) {
            return ResponseEntity.status(401).body(null); // Trả về lỗi 401 nếu không có userId
        }

        int quantityChange = request.get("quantityChange");
        Map<String, Object> response = cartService.updateQuantity(userId, productId, quantityChange);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updatePaymentMethod")
    @ResponseBody
    public ResponseEntity<String> updatePaymentMethod(@RequestBody Map<String, String> request, HttpServletRequest requestHttp) {
        Integer userId = getUserIdFromCookies(requestHttp);
        if (userId == null) {
            return ResponseEntity.status(401).body("User not authenticated"); // Trả về lỗi 401 nếu không có userId
        }

        String paymentMethod = request.get("paymentMethod");
        cartService.updatePaymentMethod(userId, paymentMethod);
        return ResponseEntity.ok("Payment method updated to " + paymentMethod);
    }

    @PostMapping("/removeItem/{productId}")
    @ResponseBody
    public ResponseEntity<String> removeItem(@PathVariable Integer productId, HttpServletRequest requestHttp) {
        Integer userId = getUserIdFromCookies(requestHttp);
        if (userId == null) {
            return ResponseEntity.status(401).body("User not authenticated"); // Trả về lỗi 401 nếu không có userId
        }

        cartService.removeItem(userId, productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/invoice")
    public String invoice(HttpServletRequest request, Model model) {
        Integer userId = getUserIdFromCookies(request);
        if (userId == null) {
            return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập nếu không có userId trong cookie
        }

        CartDto cartDto = cartService.getCartByUserId(userId);
        double totalPrice = cartService.calculateTotalPrice(cartDto);
        String invoiceNumber = "INV-" + System.currentTimeMillis(); // Tạo mã hóa đơn
        String createdDate = java.time.LocalDate.now().toString(); // Ngày tạo hóa đơn
        String dueDate = java.time.LocalDate.now().plusDays(1).toString(); // Ngày hết hạn hóa đơn

        // Giả sử lấy thông tin khách hàng từ đơn hàng
        String customerName = cartDto.getCustomerName(); // Thay thế bằng thông tin thực tế từ giỏ hàng
        String customerAddress = cartDto.getCustomerAddress(); // Thay thế bằng thông tin thực tế từ giỏ hàng

        model.addAttribute("cart", cartDto);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("invoiceNumber", invoiceNumber);
        model.addAttribute("createdDate", createdDate);
        model.addAttribute("dueDate", dueDate);
        model.addAttribute("customerName", customerName);
        model.addAttribute("customerAddress", customerAddress);
        return "cart/invoice"; // trả về view invoice.html
    }

    @GetMapping("/paymenthistory")
    public String paymentHistory(Model model) {
        return "cart/paymenthistory"; // trả về view paymenthistory.html
    }
}
