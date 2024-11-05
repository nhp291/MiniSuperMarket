package devmagic.Service;

import devmagic.Model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSV {
    List<Product> findAll();
    Product findById(Integer id);
    List<Product> finTop6Product();
    Page<Product> getall(Integer pageable);
    List<Product> searchProduct(String keyword);
    Page<Product> seachProduct(String keyword,Integer pageNo);
    Page<Product> getForAll(Integer pageable);
    Page<Product> getFiveAll(Integer pageable);

}