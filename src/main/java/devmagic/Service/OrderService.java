package devmagic.Service;

import devmagic.Model.Order;
import devmagic.Reponsitory.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository ordersRepository;

    public List<Order> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Order createOrder(Order order) {
        return ordersRepository.save(order);
    }

    public Order updateOrder(Integer id, Order order) {
        order.setOrderId(id);
        return ordersRepository.save(order);
    }

    public void deleteOrder(Integer id) {
        ordersRepository.deleteById(id);
    }
}