package devmagic.Service;

import devmagic.Model.Account;
import devmagic.Model.Cart;
import devmagic.Model.Product;
import devmagic.Reponsitory.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Optional<Cart> findByAccountAndProduct(Account account, Product product) {
        return cartRepository.findByAccountAndProduct(account, product);
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    public Cart findById(Integer cartId) {
        return cartRepository.findById(cartId).orElse(null);
    }

    public void deleteById(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    // Bổ sung phương thức lấy danh sách giỏ hàng theo accountId
    public List<Cart> findByAccountId(Integer accountId) {
        return cartRepository.findByAccount_AccountId(accountId);
    }



}

