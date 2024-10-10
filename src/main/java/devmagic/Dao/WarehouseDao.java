package devmagic.Dao;

import devmagic.Model.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WarehouseDao {
    @PersistenceContext // Sử dụng EntityManager để quản lý các entity
    private EntityManager entityManager;

    // Lấy tất cả kho hàng
    public List<Warehouse> findAll() {
        TypedQuery<Warehouse> query = entityManager.createQuery("SELECT w FROM Warehouse w", Warehouse.class);
        return query.getResultList();
    }

    // Tìm kho theo ID
    public Warehouse findById(Integer warehouseId) {
        return entityManager.find(Warehouse.class, warehouseId);
    }

    // Lưu kho hàng mới
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Warehouse save(Warehouse warehouse) {
        entityManager.persist(warehouse);
        return warehouse;
    }

    // Cập nhật kho hàng
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public Warehouse update(Warehouse warehouse) {
        return entityManager.merge(warehouse);
    }

    // Xóa kho hàng
    @Transactional // Ghi nhớ các thay đổi vào cơ sở dữ liệu
    public void delete(Integer warehouseId) {
        Warehouse warehouse = findById(warehouseId);
        if (warehouse != null) {
            entityManager.remove(warehouse);
        }
    }
}

