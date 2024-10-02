package devmagic.Reponsitory;

import devmagic.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> { // Use Integer if your ID is Integer
}

