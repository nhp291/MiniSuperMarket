package devmagic.Reponsitory;

import devmagic.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> searchProduct(String keyword);
    // Use Integer if your ID is Integerr
}

