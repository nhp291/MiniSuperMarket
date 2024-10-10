package devmagic.Dao;

import devmagic.Model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class RoleDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả vai trò
    public List<Role> findAll() {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r", Role.class);
        return query.getResultList();
    }

    // Tìm vai trò theo ID
    public Role findById(Integer roleId) {
        return entityManager.find(Role.class, roleId);
    }

    // Lưu vai trò mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Role save(Role role) {
        entityManager.persist(role);
        return role;
    }

    // Cập nhật vai trò
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Role update(Role role) {
        return entityManager.merge(role);
    }

    // Xóa vai trò
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer roleId) {
        Role role = findById(roleId);
        if (role != null) {
            entityManager.remove(role);
        }
    }
}

