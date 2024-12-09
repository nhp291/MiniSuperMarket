package devmagic.Service;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.*;
import devmagic.Reponsitory.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartItemDTO> getCartItemDTOs(Integer accountId) {
        List<Cart> carts = cartRepository.findByAccount_AccountId(accountId);
        List<CartItemDTO> dtos = new ArrayList<>();

        for (Cart cart : carts) {
            String imageUrl = cart.getProduct().getImages().get(0).getImageUrl();
            String productName = cart.getProduct().getName();
            dtos.add(new CartItemDTO(imageUrl, productName, cart.getQuantity(), cart.getPrice(), cart.getProduct()));
        }

        return dtos;
    }

    public BigDecimal calculateTotalPrice(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calculateTotalQuantity(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }

    public void clearCart(Integer accountId) {
        List<Cart> carts = cartRepository.findByAccount_AccountId(accountId);
        cartRepository.deleteAll(carts);
    }

    public Optional<Cart> findByAccountAndProduct(Account account, Product product) {
        return cartRepository.findByAccountAndProduct(account, product);
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    public Cart findById(Integer id) {
        return cartRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        cartRepository.deleteById(id);
    }
}
