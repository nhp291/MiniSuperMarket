package devmagic.Service;

import devmagic.Model.CartDto;
import devmagic.Model.CartItemDto;
import devmagic.Model.ResourceNotFoundException;
import devmagic.Model.Order;
import devmagic.Model.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrdersDetailService ordersDetailService;

    public CartDto getCartByUserId(Integer userId) {
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(order -> Objects.equals(order.getAccount().getAccountId(), userId))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user id: " + userId);
        }

        Order latestOrder = orders.get(0); // Giả sử lấy đơn hàng mới nhất
        List<OrderDetail> orderDetails = ordersDetailService.getAllOrderDetails().stream()
                .filter(detail -> Objects.equals(detail.getOrder().getOrderId(), latestOrder.getOrderId()))
                .collect(Collectors.toList());

        List<CartItemDto> items = orderDetails.stream()
                .map(detail -> new CartItemDto(
                        detail.getProduct().getProductId(),
                        detail.getProduct().getName(),
                        detail.getProduct().getImages().get(0).getImageUrl(), // Giả sử lấy hình ảnh đầu tiên
                        detail.getQuantity(),
                        detail.getPrice()
                ))
                .collect(Collectors.toList());

        String customerName = latestOrder.getAccount().getUsername(); // Lấy tên khách hàng từ tài khoản
        String customerAddress = latestOrder.getAccount().getAddress(); // Lấy địa chỉ khách hàng từ tài khoản

        return new CartDto(items, latestOrder.getPaymentMethod(), customerName, customerAddress);
    }

    public double calculateTotalPrice(CartDto cartDto) {
        return cartDto.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public Map<String, Object> updateQuantity(Integer userId, Integer productId, int quantityChange) {
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(order -> Objects.equals(order.getAccount().getAccountId(), userId))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user id: " + userId);
        }

        Order latestOrder = orders.get(0); // Giả sử lấy đơn hàng mới nhất
        List<OrderDetail> orderDetails = ordersDetailService.getAllOrderDetails().stream()
                .filter(detail -> Objects.equals(detail.getOrder().getOrderId(), latestOrder.getOrderId()))
                .collect(Collectors.toList());

        OrderDetail orderDetail = orderDetails.stream()
                .filter(detail -> Objects.equals(detail.getProduct().getProductId(), productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in order"));

        int newQuantity = orderDetail.getQuantity() + quantityChange;
        if (newQuantity < 1) {
            newQuantity = 1; // Ngăn số lượng nhỏ hơn 1
        }
        orderDetail.setQuantity(newQuantity);
        ordersDetailService.updateOrderDetail(orderDetail.getOrderDetailId(), orderDetail);

        double newPrice = orderDetail.getPrice() * newQuantity;
        double totalPrice = orderDetails.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("newQuantity", newQuantity);
        response.put("newPrice", newPrice);
        response.put("totalPrice", totalPrice);

        return response;
    }

    public void updatePaymentMethod(Integer userId, String paymentMethod) {
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(order -> Objects.equals(order.getAccount().getAccountId(), userId))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user id: " + userId);
        }

        Order latestOrder = orders.get(0); // Giả sử lấy đơn hàng mới nhất
        latestOrder.setPaymentMethod(paymentMethod);
        orderService.updateOrder(latestOrder.getOrderId(), latestOrder);
    }

    public void removeItem(Integer userId, Integer productId) {
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(order -> Objects.equals(order.getAccount().getAccountId(), userId))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user id: " + userId);
        }

        Order latestOrder = orders.get(0); // Giả sử lấy đơn hàng mới nhất
        List<OrderDetail> orderDetails = ordersDetailService.getAllOrderDetails().stream()
                .filter(detail -> Objects.equals(detail.getOrder().getOrderId(), latestOrder.getOrderId()))
                .collect(Collectors.toList());

        OrderDetail orderDetail = orderDetails.stream()
                .filter(detail -> Objects.equals(detail.getProduct().getProductId(), productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in order"));

        ordersDetailService.deleteOrderDetail(orderDetail.getOrderDetailId());
    }
}
