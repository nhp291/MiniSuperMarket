package devmagic.Repository;

import devmagic.Model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productImageRepository extends JpaRepository<ProductImage, Integer> {
}