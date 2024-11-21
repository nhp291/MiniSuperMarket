package devmagic.Controller.Admin;

import devmagic.Model.Order;
import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/Orders")
public class OrderController {
    @Autowired
    private OrderService ordersService;

    @GetMapping("/OrderList")
    public String OrderList(Model model) {
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
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        ordersService.deleteOrder(id);
        return ResponseEntity.noContent().build();
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
}

