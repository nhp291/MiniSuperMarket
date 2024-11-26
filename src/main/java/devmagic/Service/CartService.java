package devmagic.Service;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.Account;
import devmagic.Model.Cart;
import devmagic.Model.Product;
import devmagic.Reponsitory.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    // Constructor injection for CartRepository
    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartItemDTO> getUpdatedCart(Integer accountId) {
        return getCartItemDTOs(accountId);  // Trả về danh sách sản phẩm trong giỏ hàng dưới dạng DTO
    }

    // Find cart item by account and product
    public Optional<Cart> findByAccountAndProduct(Account account, Product product) {
        return cartRepository.findByAccountAndProduct(account, product);
    }

    // Save a cart item
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    // Find cart by cartId
    public Cart findById(Integer cartId) {
        return cartRepository.findById(cartId).orElse(null);
    }

    // Delete a cart by cartId
    public void deleteById(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    // Get a list of cart items by accountId
    public List<Cart> getCartItemsByAccountId(Integer accountId) {
        return cartRepository.findByAccount_AccountId(accountId);
    }

    // Calculate the total price of cart items
    public double calculateTotalPrice(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .mapToDouble(cart -> cart.getPrice() * cart.getQuantity())
                .sum();
    }

    // Calculate the total quantity of items in the cart
    public int calculateTotalQuantity(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }

    // Convert cart items to CartItemDTOs
    public List<CartItemDTO> getCartItemDTOs(Integer accountId) {
        List<Cart> cartItems = getCartItemsByAccountId(accountId);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (Cart cart : cartItems) {
            String imageUrl = cart.getProduct().getImages().get(0).getImageUrl(); // Giả sử hình ảnh đầu tiên là hình chính
            String productName = cart.getProduct().getName();
            int quantity = cart.getQuantity();
            double price = cart.getPrice();
            Product product = cart.getProduct(); // Lấy đối tượng Product từ Cart
            cartItemDTOs.add(new CartItemDTO(imageUrl, productName, quantity, price, product));
        }
        return cartItemDTOs;
    }

    // Clear cart items for a given accountId
    public void clearCart(Integer accountId) {
        List<Cart> cartItems = cartRepository.findByAccount_AccountId(accountId);
        cartRepository.deleteAll(cartItems);
    }
}
