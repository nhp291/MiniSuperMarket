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

    // Tìm một mục giỏ hàng theo account và product
    Optional<Cart> findByAccountAndProduct(Account account, Product product);

    // Lấy danh sách giỏ hàng theo accountId
    List<Cart> findByAccount_AccountId(Integer accountId);

    // Tìm mục giỏ hàng theo productId và accountId
    Optional<Cart> findByAccount_AccountIdAndProduct_ProductId(Integer accountId, Integer productId);

    // Tìm mục giỏ hàng theo productId (nếu cần sử dụng riêng)
    List<Cart> findAllByProduct_ProductId(Integer productId);
}

