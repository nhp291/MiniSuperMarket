package devmagic.Dao;

import devmagic.Model.AccountAuthProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountAuthProviderDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả liên kết tài khoản với nhà cung cấp
    public List<AccountAuthProvider> findAll() {
        TypedQuery<AccountAuthProvider> query = entityManager.createQuery("SELECT aap FROM AccountAuthProvider aap", AccountAuthProvider.class);
        return query.getResultList();
    }

    // Tìm liên kết theo ID
    public AccountAuthProvider findById(Integer accountAuthId) {
        return entityManager.find(AccountAuthProvider.class, accountAuthId);
    }

    // Lưu liên kết mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public AccountAuthProvider save(AccountAuthProvider accountAuthProvider) {
        entityManager.persist(accountAuthProvider);
        return accountAuthProvider;
    }

    // Cập nhật liên kết
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public AccountAuthProvider update(AccountAuthProvider accountAuthProvider) {
        return entityManager.merge(accountAuthProvider);
    }

    // Xóa liên kết
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer accountAuthId) {
        AccountAuthProvider accountAuthProvider = findById(accountAuthId);
        if (accountAuthProvider != null) {
            entityManager.remove(accountAuthProvider);
        }
    }
}

