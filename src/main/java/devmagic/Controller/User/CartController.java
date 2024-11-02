package devmagic.Controller.User;

import devmagic.Service.CartService;
import devmagic.Dto.CartDto;
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

    @GetMapping("/shoppingcart/{userId}")
    public String shoppingCart(@PathVariable Integer userId, Model model) {
        CartDto cartDto = cartService.getCartByUserId(userId);
        double totalPrice = cartService.calculateTotalPrice(cartDto);
        model.addAttribute("cart", cartDto);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/shoppingcart"; // trả về view shoppingcart.html
    }

    @PostMapping("/updateQuantity/{userId}/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(@PathVariable Integer userId, @PathVariable Integer productId, @RequestBody Map<String, Integer> request) {
        int quantityChange = request.get("quantityChange");
        Map<String, Object> response = cartService.updateQuantity(userId, productId, quantityChange);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updatePaymentMethod/{userId}")
    @ResponseBody
    public ResponseEntity<String> updatePaymentMethod(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        String paymentMethod = request.get("paymentMethod");
        cartService.updatePaymentMethod(userId, paymentMethod);
        return ResponseEntity.ok("Payment method updated to " + paymentMethod);
    }

    @PostMapping("/removeItem/{userId}/{productId}")
    @ResponseBody
    public ResponseEntity<String> removeItem(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/invoice/{userId}")
    public String invoice(@PathVariable Integer userId, Model model) {
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
