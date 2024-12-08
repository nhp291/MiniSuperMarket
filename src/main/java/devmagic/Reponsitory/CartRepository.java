    package devmagic.Reponsitory;
    import devmagic.Model.Account;
    import devmagic.Model.Cart;
    import devmagic.Model.Product;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface CartRepository extends JpaRepository<Cart, Integer> {
        Optional<Cart> findByAccountAndProduct(Account account, Product product);

        // Thêm phương thức để lấy danh sách giỏ hàng theo accountId
        List<Cart> findByAccount_AccountId(Integer accountId);
    }


