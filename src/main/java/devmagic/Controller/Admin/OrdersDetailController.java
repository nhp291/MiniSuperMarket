package devmagic.Controller.Admin;

import devmagic.Model.OrderDetail;
import devmagic.Service.OrdersDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
public class OrdersDetailController {
    @Autowired
    private OrdersDetailService ordersDetailService;

    @GetMapping
    public List<OrderDetail> getAllOrderDetails() {
        return ordersDetailService.getAllOrderDetails();
    }

    @PostMapping
    public OrderDetail createOrderDetail(@RequestBody OrderDetail orderDetail) {
        return ordersDetailService.createOrderDetail(orderDetail);
    }

    @PutMapping("/{id}")
    public OrderDetail updateOrderDetail(@PathVariable Integer id, @RequestBody OrderDetail orderDetail) {
        return ordersDetailService.updateOrderDetail(id, orderDetail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Integer id) {
        ordersDetailService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }
}

