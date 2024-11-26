package devmagic.Controller.User;


import devmagic.Model.Order;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PaymenthistoryController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/cart/Paymenthistory")
    public String getPaymentHistory(HttpSession session, Model model) {
        // Lấy accountId từ session
        Integer accountId = (Integer) session.getAttribute("accountId");
        if (accountId == null) {
            return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }

        // Lấy danh sách đơn hàng theo accountId
        List<Order> orders = orderService.getOrdersByAccountId(accountId);

        // Đưa danh sách đơn hàng vào model
        model.addAttribute("orders", orders);

        // Trả về trang hiển thị
        return "payment_history";
    }
}
