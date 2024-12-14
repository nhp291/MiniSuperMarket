package devmagic.Controller.User;

import devmagic.Model.Order;
import devmagic.Model.OrderDetail;
import devmagic.Model.Product;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class OrderScheduler {

    private final OrderService orderService;
    private final ProductRepository productRepository;

    @Autowired
    public OrderScheduler(OrderService orderService, ProductRepository productRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    // Phương thức tự động chạy mỗi 5 phút
    @Scheduled(cron = "0 */5 * * * *")
    public void checkPendingOrders() {
        List<Order> pendingOrders = orderService.getAllOrders().stream()
                .filter(order -> "PENDING".equals(order.getPaymentStatus()) &&
                        order.getOrderDate().before(new Date(System.currentTimeMillis() - 30 * 60 * 1000)))  // Kiểm tra nếu đơn hàng đã quá 30 phút, tạo ra thời điểm cách hiện tại 30 phút về trước.
                .collect(Collectors.toList());

        for (Order order : pendingOrders) {
            // Cập nhật trạng thái thành CANCELLED
            orderService.updatePaymentStatus(order.getOrderId(), "CANCELLED");

            // Cập nhật lại số lượng sản phẩm vào kho
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Product product = orderDetail.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderDetail.getQuantity());
                productRepository.save(product);
            }
        }
    }
}
