package devmagic.Reponsitory;

import devmagic.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Lấy danh sách đơn hàng theo tài khoản
    List<Order> findByAccount_AccountId(Integer accountId);

    // Thống kê tổng số đơn hàng
    @Query("SELECT COUNT(o) FROM Order o")
    Long countTotalOrders();

    // Tổng doanh thu từ các đơn hàng
    @Query("SELECT SUM(od.price * od.quantity) FROM Order o JOIN o.orderDetails od")
    Double calculateTotalRevenue();

    // Lấy danh sách đơn hàng theo tháng
    @Query("SELECT o FROM Order o WHERE FUNCTION('MONTH', o.orderDate) = :month")
    List<Order> findOrdersByMonth(int month);

    // Doanh thu theo từng ngày
    @Query("SELECT DATE(o.orderDate) AS date, SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "GROUP BY DATE(o.orderDate)")
    List<Object[]> getRevenueByDay();

    // Doanh thu theo từng tháng
    @Query("SELECT FUNCTION('MONTH', o.orderDate) AS month, FUNCTION('YEAR', o.orderDate) AS year, " +
            "SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
    List<Object[]> getRevenueByMonth();

    // Doanh thu theo từng năm
    @Query("SELECT FUNCTION('YEAR', o.orderDate) AS year, SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "GROUP BY FUNCTION('YEAR', o.orderDate)")
    List<Object[]> getRevenueByYear();

    // Tìm đơn hàng không bị xoá
    @Query("SELECT o FROM Order o WHERE o.isDeleted = false")
    List<Order> findAllActiveOrders();
}
