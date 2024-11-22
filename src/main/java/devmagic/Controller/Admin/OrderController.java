package devmagic.Controller.Admin;

import devmagic.Dto.PaymentStatusUpdateRequestDTO;
import devmagic.Model.Order;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Controller
@RequestMapping("/Orders")
public class OrderController {

    @Autowired
    private OrderService ordersService;

    // Queue để gửi sự kiện từ server đến client qua SSE
    private final BlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();

    // Lắng nghe sự kiện từ server
    @GetMapping(value = "/Orders/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody String sendSseEvents() {
        try {
            return eventQueue.take();  // Lấy sự kiện từ Queue
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @GetMapping("/OrderList")
    public String OrderList(Model model) {
        List<Order> orders = ordersService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Order List Page");
        model.addAttribute("viewName", "admin/menu/OrderList");
        return "admin/layout";
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return ordersService.createOrder(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        return ordersService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer id) {
        try {
            ordersService.deleteOrder(id);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/orders/total")
    public Long getTotalOrders() {
        return ordersService.getTotalOrders();
    }

    @GetMapping("/orders/revenue")
    public Double getTotalRevenue() {
        return ordersService.getTotalRevenue();
    }

    @GetMapping("/orders/revenue-by-month")
    public List<Object[]> getRevenueByMonth() {
        return ordersService.getRevenueByMonth();
    }

    //Hàm xử l ngoại lệ
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Cập nhật trạng thái thanh toán và gửi SSE cho frontend
    @PostMapping("/ChangePaymentStatus")
    public String changePaymentStatus(@RequestParam int orderId, @RequestParam String paymentStatus) {
        try {
            // Cập nhật trạng thái thanh toán
            ordersService.updatePaymentStatus(orderId, paymentStatus);

            // Thông báo qua SSE
            eventQueue.put("Payment status updated for Order ID: " + orderId + " to " + paymentStatus);

            return "redirect:/Orders/OrderList";
        } catch (Exception e) {
            return "error";  // Trả về lỗi nếu có
        }
    }
}

