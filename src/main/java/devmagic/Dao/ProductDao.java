package devmagic.Dao;

import devmagic.Model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả sản phẩm
    public List<Product> findAll() {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p", Product.class);
        return query.getResultList();
    }

    // Tìm sản phẩm theo ID
    public Product findById(Integer productId) {
        return entityManager.find(Product.class, productId);
    }

    // Lưu sản phẩm mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Product save(Product product) {
        entityManager.persist(product);
        return product;
    }

    // Cập nhật sản phẩm
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Product update(Product product) {
        return entityManager.merge(product);
    }

    // Xóa sản phẩm
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer productId) {
        Product product = findById(productId);
        if (product != null) {
            entityManager.remove(product);
        }
    }
}

