package devmagic.Controller.User;

import devmagic.Model.Order;
import devmagic.Service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class PaymenthistoryController {

    private final OrderService orderService;

    @Autowired
    public PaymenthistoryController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/paymenthistory")
    public String getPaymentHistory(HttpServletRequest request, Model model) {
        Integer accountId = getAccountIdFromSession(request);

        if (accountId == null) {
            return "redirect:/user/login";
        }

        String username = getAuthenticatedUsername(request);
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

    private String getAuthenticatedUsername(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object usernameObj = session.getAttribute("username");
        return (usernameObj instanceof String) ? (String) usernameObj : null;
    }
}
