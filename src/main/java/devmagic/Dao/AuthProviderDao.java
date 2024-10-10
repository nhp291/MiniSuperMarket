package devmagic.Dao;

import devmagic.Model.AuthProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthProviderDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả nhà cung cấp
    public List<AuthProvider> findAll() {
        TypedQuery<AuthProvider> query = entityManager.createQuery("SELECT ap FROM AuthProvider ap", AuthProvider.class);
        return query.getResultList();
    }

    // Tìm nhà cung cấp theo ID
    public AuthProvider findById(Integer providerId) {
        return entityManager.find(AuthProvider.class, providerId);
    }

    // Lưu nhà cung cấp mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public AuthProvider save(AuthProvider authProvider) {
        entityManager.persist(authProvider);
        return authProvider;
    }

    // Cập nhật nhà cung cấp
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public AuthProvider update(AuthProvider authProvider) {
        return entityManager.merge(authProvider);
    }

    // Xóa nhà cung cấp
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer providerId) {
        AuthProvider authProvider = findById(providerId);
        if (authProvider != null) {
            entityManager.remove(authProvider);
        }
    }
}

