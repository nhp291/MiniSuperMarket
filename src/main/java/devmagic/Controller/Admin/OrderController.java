package devmagic.Controller.Admin;

import devmagic.Model.Order;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // Hiển thị danh sách đơn hàng trên giao diện
    @GetMapping("/OrderList")
    public String OrderList(Model model) {
        try {
            List<Order> orders = ordersService.getAllActiveOrders();
            model.addAttribute("orders", orders);
            model.addAttribute("pageTitle", "Order List Page");
            model.addAttribute("viewName", "admin/menu/OrderList");
            return "admin/layout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching orders: " + e.getMessage());
            return "error";
        }
    }

    // API: Lấy danh sách đơn hàng (JSON)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = ordersService.getAllActiveOrders();
            if (orders.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về 204 nếu không có dữ liệu
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Trả về lỗi 500
        }
    }

    // Tạo đơn hàng mới
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = ordersService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Cập nhật thông tin đơn hàng
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        try {
            Order updatedOrder = ordersService.updateOrder(id, order);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Xóa đơn hàng (đánh dấu là đã xóa)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer id) {
        try {
            ordersService.deleteOrder(id);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Tổng số đơn hàng
    @GetMapping("/orders/total")
    public ResponseEntity<Long> getTotalOrders() {
        try {
            Long totalOrders = ordersService.getTotalOrders();
            return ResponseEntity.ok(totalOrders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Tổng doanh thu
    @GetMapping("/orders/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        try {
            Double revenue = ordersService.getTotalRevenue();
            return ResponseEntity.ok(revenue);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Doanh thu theo tháng
    @GetMapping("/orders/revenue-by-month")
    public ResponseEntity<List<Object[]>> getRevenueByMonth() {
        try {
            List<Object[]> revenueByMonth = ordersService.getRevenueByMonth();
            return ResponseEntity.ok(revenueByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
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

    // Xử lý ngoại lệ
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
