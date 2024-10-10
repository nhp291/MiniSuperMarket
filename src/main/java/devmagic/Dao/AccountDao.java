package devmagic.Dao;

import devmagic.Model.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả tài khoản
    public List<Account> findAll() {
        TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM Account a", Account.class);
        return query.getResultList();
    }

    // Tìm tài khoản theo ID
    public Account findById(Integer accountId) {
        return entityManager.find(Account.class, accountId);
    }

    // Lưu tài khoản mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Account save(Account account) {
        entityManager.persist(account);
        return account;
    }

    // Cập nhật tài khoản
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Account update(Account account) {
        return entityManager.merge(account);
    }

    // Xóa tài khoản
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer accountId) {
        Account account = findById(accountId);
        if (account != null) {
            entityManager.remove(account);
        }
    }
}
