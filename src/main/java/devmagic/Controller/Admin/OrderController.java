package devmagic.Controller.Admin;

import devmagic.Model.Order;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService ordersService;

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
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        ordersService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

