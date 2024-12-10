package devmagic.Reponsitory;

import devmagic.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Lấy danh sách đơn hàng không bị xóa
    List<Order> findAllByIsDeleted(boolean isDeleted);

    // Lấy danh sách đơn hàng theo tài khoản
    List<Order> findByAccount_AccountId(Integer accountId);

    // Lấy danh sách đơn hàng với phân trang và lọc theo trạng thái xóa
    Page<Order> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

    // Tổng số đơn hàng
    @Query("SELECT COUNT(o) FROM Order o WHERE o.isDeleted = false")
    Long countTotalOrders();

    // Tổng doanh thu từ các đơn hàng
    @Query("SELECT SUM(od.price * od.quantity) FROM Order o JOIN o.orderDetails od WHERE o.isDeleted = false")
    BigDecimal calculateTotalRevenue();

    // Đếm số đơn hàng theo trạng thái thanh toán
    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = :paymentStatus")
    Long countByPaymentStatus(@Param("paymentStatus") String paymentStatus);

    // Doanh thu theo tháng
    @Query("SELECT FUNCTION('MONTH', o.orderDate) AS month, FUNCTION('YEAR', o.orderDate) AS year, " +
            "SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.isDeleted = false " +
            "GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
    List<Object[]> getRevenueByMonth();

    // Thống kê theo ngày
    @Query("SELECT DATE(o.orderDate) AS date, COUNT(o) AS orderCount, SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.isDeleted = false " +
            "GROUP BY DATE(o.orderDate) ORDER BY date")
    List<Object[]> getOrdersAndRevenueByDay();

    // Thống kê theo tháng và năm
    @Query("SELECT FUNCTION('MONTH', o.orderDate) AS month, FUNCTION('YEAR', o.orderDate) AS year, " +
            "SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.isDeleted = false " +
            "GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
    List<Object[]> getRevenueByMonthAndYear();

    // Thống kê doanh thu theo năm
    @Query("SELECT FUNCTION('YEAR', o.orderDate) AS year, SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.isDeleted = false " +
            "GROUP BY FUNCTION('YEAR', o.orderDate) ORDER BY year")
    List<Object[]> getRevenueByYear();

}
