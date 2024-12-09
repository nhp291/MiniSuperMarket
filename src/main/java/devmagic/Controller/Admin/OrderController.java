package devmagic.Controller.Admin;

import devmagic.Model.Order;
import devmagic.Reponsitory.OrderRepository;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Controller
@RequestMapping("/Orders")
public class OrderController {

    @Autowired
    private OrderService ordersService;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    public OrderController(OrderService orderService) {
        this.ordersService = orderService;
    }

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
    public String OrderList(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {
        // Phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> ordersPage = ordersService.getOrdersByPage(pageable);

        // Thêm thông tin phân trang vào model
        model.addAttribute("orders", ordersPage.getContent()); // Danh sách đơn hàng
        model.addAttribute("currentPage", page);               // Trang hiện tại
        model.addAttribute("totalPages", ordersPage.getTotalPages()); // Tổng số trang
        model.addAttribute("pageSize", size);                  // Kích thước trang
        model.addAttribute("pageTitle", "Order List Page");
        model.addAttribute("viewName", "admin/menu/OrderList");

        return "admin/layout";
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

    //Xóa hẳn đơn hàng khỏi cơ sở dữ liệu
    @DeleteMapping("/{id}")
    public ModelAndView deleteOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ordersService.deleteOrder(id);
//            redirectAttributes.addFlashAttribute("message", "Order deleted successfully.");
            return new ModelAndView("redirect:/Orders/OrderList");  // Sử dụng redirect đúng

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Order not found.");
            return new ModelAndView("redirect:/Orders/OrderList");  // Sử dụng redirect đúng
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
    @PostMapping("/{orderId}/change-payment-status")
    public String changePaymentStatus( @PathVariable int orderId,
            @RequestParam String paymentStatus, Model model) {
        try {
            if (paymentStatus == null || paymentStatus.isEmpty()) {
                throw new IllegalArgumentException("Payment status cannot be null or empty");
            }

            ordersService.updatePaymentStatus(orderId, paymentStatus);

            List<Order> orders = ordersService.getAllOrders();
            model.addAttribute("orders", orders);

            model.addAttribute("message", "Payment status updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update payment status: " + e.getMessage());
        }

        model.addAttribute("pageTitle", "Order List Page");
        model.addAttribute("viewName", "admin/menu/OrderList");
        return "admin/layout";
    }

    public void updatePaymentStatus(int orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái hợp lệ
        if (!List.of("PENDING", "PROCESSING", "COMPLETED", "CANCELLED").contains(paymentStatus)) {
            throw new IllegalArgumentException("Invalid payment status: " + paymentStatus);
        }

        // Cập nhật trạng thái thanh toán
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }

    // Xử lý ngoại lệ
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Thống kê trạng thái thanh toán
    @GetMapping("/payment-status-stats")
    @ResponseBody
    public Map<String, Long> getPaymentStatusStats() {
        return ordersService.getPaymentStatusStatistics();
    }

}
