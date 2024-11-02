package devmagic.Service;

import devmagic.Model.OrderDetail;
import devmagic.Reponsitory.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersDetailService {
    @Autowired
    private OrderDetailRepository ordersDetailRepository;

    public List<OrderDetail> getAllOrderDetails() {
        return ordersDetailRepository.findAll();
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        return ordersDetailRepository.save(orderDetail);
    }

    public OrderDetail updateOrderDetail(Integer id, OrderDetail orderDetail) {
        orderDetail.setOrderDetailId(id);
        return ordersDetailRepository.save(orderDetail);
    }

    public void deleteOrderDetail(Integer id) {
        ordersDetailRepository.deleteById(id);
    }
}