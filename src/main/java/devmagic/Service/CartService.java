package devmagic.Service;

import devmagic.Dto.CartItemDTO;
import devmagic.Model.*;
import devmagic.Reponsitory.CartRepository;
import devmagic.Reponsitory.AccountRepository;  // Thêm AccountRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;  // Khai báo AccountRepository

    @Autowired
    public CartService(CartRepository cartRepository, AccountRepository accountRepository) {
        this.cartRepository = cartRepository;
        this.accountRepository = accountRepository;  // Khởi tạo AccountRepository
    }

    // Lấy thông tin tài khoản từ database theo accountId
    public Optional<Account> getAccountById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    // Lấy danh sách CartItemDTO cho giỏ hàng của người dùng
    public List<CartItemDTO> getCartItemDTOs(Integer accountId) {
        List<Cart> carts = cartRepository.findByAccount_AccountId(accountId);
        List<CartItemDTO> dtos = new ArrayList<>();

        for (Cart cart : carts) {
            // Lấy thông tin sản phẩm từ Cart
            String imageUrl = cart.getProduct().getImages().get(0).getImageUrl(); // Lấy URL của ảnh sản phẩm
            String productName = cart.getProduct().getName();  // Lấy tên sản phẩm
            BigDecimal price = calculateEffectivePrice(cart.getProduct()); // Tính giá hiệu quả sản phẩm (nếu có giảm giá)

            // Tạo CartItemDTO và thêm vào danh sách
            dtos.add(new CartItemDTO(imageUrl, productName, cart.getQuantity(), price, cart.getProduct()));
        }

        return dtos;
    }

    // Tính tổng giá trị của giỏ hàng (tổng tiền của tất cả sản phẩm)
    public BigDecimal calculateTotalPrice(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Tính tổng số lượng của tất cả sản phẩm trong giỏ hàng
    public int calculateTotalQuantity(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }

    // Xóa toàn bộ giỏ hàng của người dùng
    public void clearCart(Integer accountId) {
        List<Cart> carts = cartRepository.findByAccount_AccountId(accountId);
        cartRepository.deleteAll(carts);
    }

    // Tìm Cart theo tài khoản và sản phẩm
    public Optional<Cart> findByAccountAndProduct(Account account, Product product) {
        return cartRepository.findByAccountAndProduct(account, product);
    }

    // Lưu thông tin Cart vào cơ sở dữ liệu
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    // Tìm Cart theo id
    public Cart findById(Integer id) {
        return cartRepository.findById(id).orElse(null);
    }

    // Xóa Cart theo id
    public void deleteById(Integer id) {
        cartRepository.deleteById(id);
    }

    // Cập nhật giá hiệu quả (tính giá bán nếu có khuyến mãi)
    public BigDecimal calculateEffectivePrice(Product product) {
        // Nếu sản phẩm có khuyến mãi, trả giá khuyến mãi
        return (product.getSale() != null && product.getSale().compareTo(BigDecimal.ZERO) > 0)
                ? product.getSale()
                : product.getPrice();  // Nếu không có khuyến mãi, trả giá gốc
    }
}
