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
    @Query("SELECT MONTH(o.orderDate) AS month, YEAR(o.orderDate) AS year, " +
            "SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.isDeleted = false " +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> getRevenueByMonth();

    // Thống kê theo ngày
    @Query("SELECT CAST(o.orderDate AS date) AS date, COUNT(o) AS totalOrders, SUM(od.price * od.quantity) AS totalRevenue " +
            "FROM Order o JOIN o.orderDetails od WHERE o.isDeleted = false GROUP BY CAST(o.orderDate AS date) ORDER BY date")
    List<Object[]> getDailyStatistics();

    // Thống kê theo tháng
    @Query("SELECT YEAR(o.orderDate) AS year, MONTH(o.orderDate) AS month, COUNT(o) AS totalOrders, SUM(od.price * od.quantity) AS totalRevenue " +
            "FROM Order o JOIN o.orderDetails od WHERE o.isDeleted = false GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyStatistics();

    // Thống kê theo năm
    @Query("SELECT YEAR(o.orderDate) AS year, COUNT(o) AS totalOrders, SUM(od.price * od.quantity) AS totalRevenue " +
            "FROM Order o JOIN o.orderDetails od WHERE o.isDeleted = false GROUP BY YEAR(o.orderDate) ORDER BY year DESC")
    List<Object[]> getYearlyStatistics();


}
