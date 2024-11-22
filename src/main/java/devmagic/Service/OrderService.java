package devmagic.Service;

import devmagic.Model.Order;
import devmagic.Model.PaymentStatus;
import devmagic.Reponsitory.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository ordersRepository;

    public List<Order> getAllOrders() {
        return ordersRepository.findAll().stream()
                .filter(order -> !order.isDeleted())
                .collect(Collectors.toList());
    }

    public Order createOrder(Order order) {
        return ordersRepository.save(order);
    }

    public Order updateOrder(Integer id, Order order) {
        order.setOrderId(id);
        return ordersRepository.save(order);
    }

    public void deleteOrder(Integer id) {
        Order order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDeleted(true);
        ordersRepository.save(order);
    }

    public Long getTotalOrders() {
        return ordersRepository.countTotalOrders();
    }

    public Double getTotalRevenue() {
        return ordersRepository.calculateTotalRevenue();
    }

    public List<Object[]> getRevenueByMonth() {
        return ordersRepository.getRevenueByMonth();
    }

    public void updatePaymentStatus(int orderId, String paymentStatus) {
        Order order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(paymentStatus);
        ordersRepository.save(order);
    }

}
