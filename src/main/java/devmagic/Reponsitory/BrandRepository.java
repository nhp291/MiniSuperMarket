package devmagic.Reponsitory;

import devmagic.Model.Brand;
import devmagic.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    long count();
}
