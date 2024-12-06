package devmagic.Service;

import devmagic.Model.Order;
import devmagic.Model.Product;
import devmagic.Reponsitory.OrderRepository;
import devmagic.Reponsitory.OrderDetailRepository;
import devmagic.Reponsitory.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> !order.isDeleted())
                .collect(Collectors.toList());
    }

    // Lấy danh sách đơn hàng không bị xóa
    public List<Order> getAllActiveOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> !order.isDeleted())
                .collect(Collectors.toList());
    }

    // Tạo đơn hàng mới
    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        order.getOrderDetails().forEach(orderDetail -> {
            orderDetail.setOrder(savedOrder);
            orderDetailRepository.save(orderDetail);

            // Cập nhật số lượng sản phẩm trong kho
            Product product = orderDetail.getProduct();
            product.setStockQuantity(product.getStockQuantity() - orderDetail.getQuantity());
            productRepository.save(product);
        });
        return savedOrder;
    }

    // Cập nhật đơn hàng
    public Order updateOrder(Integer id, Order order) {
        order.setOrderId(id);
        return orderRepository.save(order);
    }

    // Xóa đơn hàng (đánh dấu là đã xóa)
    public void deleteOrder(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDeleted(true);
        orderRepository.save(order);
    }

    // Tổng số đơn hàng
    public Long getTotalOrders() {
        return orderRepository.countTotalOrders();
    }

    // Tổng doanh thu
    public Double getTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }

    // Doanh thu theo tháng
    public List<Object[]> getRevenueByMonth() {
        return orderRepository.getRevenueByMonth();
    }

    // Cập nhật trạng thái thanh toán
    public void updatePaymentStatus(int orderId, String paymentStatus) {
        // Kiểm tra đơn hàng có tồn tại hay không
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new RuntimeException("Order not found")
        );

        // Cập nhật trạng thái thanh toán
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }

    // Lấy danh sách đơn hàng theo accountId
    public List<Order> getOrdersByAccountId(int accountId) {
        return orderRepository.findByAccount_AccountId(accountId);
    }

    public Page<Order> getOrdersByPage(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order findById(int orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }


}
