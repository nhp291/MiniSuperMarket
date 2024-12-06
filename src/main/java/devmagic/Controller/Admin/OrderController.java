package devmagic.Controller.Admin;

import devmagic.Model.Order;
import devmagic.Reponsitory.OrderRepository;
import devmagic.Service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private OrderRepository orderRepository;

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
    @PostMapping("/{orderId}/change-payment-status")
    public String changePaymentStatus(
            @PathVariable int orderId,
            @RequestParam String paymentStatus,
            Model model) {
        try {
            // Kiểm tra giá trị paymentStatus không null và hợp lệ
            if (paymentStatus == null || paymentStatus.isEmpty()) {
                throw new IllegalArgumentException("Payment status cannot be null or empty");
            }

            // Cập nhật trạng thái thanh toán
            ordersService.updatePaymentStatus(orderId, paymentStatus);

            // Lấy lại danh sách đơn hàng sau khi cập nhật
            List<Order> orders = ordersService.getAllOrders();
            model.addAttribute("orders", orders);

            // Thêm thông báo thành công
            model.addAttribute("message", "Payment status updated successfully!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            model.addAttribute("error", "Failed to update payment status: " + e.getMessage());
        }

        model.addAttribute("pageTitle", "Order List Page");
        model.addAttribute("viewName", "admin/menu/OrderList");
        return "admin/layout"; // Trả về lại trang danh sách
    }

    public void updatePaymentStatus(int orderId, String paymentStatus) {
        // Kiểm tra đơn hàng có tồn tại hay không
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái hợp lệ
        if (!List.of("PENDING", "PROCESSING", "COMPLETED", "CANCELLED").contains(paymentStatus)) {
            throw new IllegalArgumentException("Invalid payment status: " + paymentStatus);
        }

        // Cập nhật trạng thái thanh toán
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }
}