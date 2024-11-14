package devmagic.Reponsitory;

import devmagic.Model.Product;
import devmagic.Model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProduct_ProductId(int productId);
    void deleteAllByProduct(Product product);
}
