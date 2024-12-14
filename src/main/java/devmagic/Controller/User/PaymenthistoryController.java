package devmagic.Controller.User;

import devmagic.Model.Order;
import devmagic.Service.CartService;
import devmagic.Service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class PaymenthistoryController {

    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public PaymenthistoryController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/paymenthistory")
    public String getPaymentHistory(HttpServletRequest request, Model model) {
        Integer accountId = getAccountIdFromSession(request);
        // Lấy số lượng sản phẩm trong giỏ hàng
        int totalQuantity = cartService.calculateTotalQuantity(cartService.getCartItemDTOs(accountId));

        // Truyền số lượng vào model để sử dụng trong template
        model.addAttribute("totalQuantity", totalQuantity);
        if (accountId == null) {
            return "redirect:/user/login";
        }

        String username = getAuthenticatedUsername();
        model.addAttribute("username", username != null ? username : "");

        List<Order> orders = orderService.getOrdersByAccountId(accountId);

        if (orders == null || orders.isEmpty()) {
            model.addAttribute("message", "Bạn chưa có hóa đơn nào.");
        } else {
            model.addAttribute("orders", orders);
        }

        return "cart/paymenthistory";
    }

    private Integer getAccountIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object accountIdObj = session.getAttribute("accountId");

        if (accountIdObj instanceof Integer) {
            return (Integer) accountIdObj;
        } else if (accountIdObj instanceof String) {
            try {
                return Integer.parseInt((String) accountIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
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
}

