package devmagic.Dao;

import devmagic.Model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả danh mục
    public List<Category> findAll() {
        TypedQuery<Category> query = entityManager.createQuery("SELECT c FROM Category c", Category.class);
        return query.getResultList();
    }

    // Tìm danh mục theo ID
    public Category findById(Integer categoryId) {
        return entityManager.find(Category.class, categoryId);
    }

    // Lưu danh mục mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Category save(Category category) {
        entityManager.persist(category);
        return category;
    }

    // Cập nhật danh mục
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Category update(Category category) {
        return entityManager.merge(category);
    }

    // Xóa danh mục
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer categoryId) {
        Category category = findById(categoryId);
        if (category != null) {
            entityManager.remove(category);
        }
    }
}

