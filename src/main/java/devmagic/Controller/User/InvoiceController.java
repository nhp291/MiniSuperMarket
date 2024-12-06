package devmagic.Controller.User;

import devmagic.Model.Order;
import devmagic.Model.OrderDetail;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class InvoiceController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/invoice/{orderId}")
    public String getInvoice(@PathVariable("orderId") int orderId, Model model) {
        // Lấy thông tin hóa đơn dựa vào orderId
        Order order = orderService.findById(orderId);
        if (order == null) {
            // Xử lý nếu không tìm thấy hóa đơn
            return "redirect:/error";
        }

        // Lấy danh sách chi tiết đơn hàng
        List<OrderDetail> orderDetails = order.getOrderDetails();

        // Tính tổng tiền
        BigDecimal total = orderDetails.stream()
                .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Gắn dữ liệu vào model
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("total", total);

        return "cart/invoice";
    }
}
