package devmagic.Reponsitory;

import devmagic.Model.Category;
import devmagic.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    long count();
}
