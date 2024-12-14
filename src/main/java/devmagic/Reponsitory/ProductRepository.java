package devmagic.Reponsitory;

import devmagic.Model.Brand;
import devmagic.Model.Category;
import devmagic.Model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Tìm sản phẩm theo mô tả (description) hoặc tên (name)
    @Query("SELECT p FROM Product p WHERE p.description LIKE %?1% OR p.name LIKE %?1%")
    List<Product> searchProduct(String keyword);

    @Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1")
    List<Product> findByCategoryId(String cid);
    // Use Integer if your ID is Integerr
    @Query("SELECT COUNT(p) FROM Product p")
    long countProducts();
    // Lọc theo giá, danh mục và từ khóa
    Page<Product> findByPriceBetweenAndCategory_CategoryIdAndNameContaining(Double minPrice, Double maxPrice, Integer categoryId, String keyword, Pageable pageable);

    // Lọc theo giá và từ khóa
    Page<Product> findByPriceBetweenAndNameContaining(Double minPrice, Double maxPrice, String keyword, Pageable pageable);

    // Lọc theo giá và danh mục
    Page<Product> findByPriceBetweenAndCategory_CategoryId(Double minPrice, Double maxPrice, Integer categoryId, Pageable pageable);


    // Lọc theo danh mục và từ khóa
    Page<Product> findByCategory_CategoryIdAndNameContaining(Integer categoryId, String keyword, Pageable pageable);

    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

    Page<Product> findByBrand_BrandId(Integer brandId, Pageable pageable);

    Page<Product> findByCategory_CategoryIdAndBrand_BrandId(Integer categoryId, Integer brandId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByNameContaining(String name, Pageable pageable);

    Page<Product> findByNameContainingAndCategory_CategoryId(String name, Integer categoryId, Pageable pageable);

    Page<Product> findByNameContainingAndBrand_BrandId(String name, Integer brandId, Pageable pageable);

    Page<Product> findByNameContainingAndCategory_CategoryIdAndBrand_BrandId(String name, Integer categoryId, Integer brandId, Pageable pageable);

    // Danh sách sản phẩm sắp hết hàng (<= 5 sản phẩm nhưng > 0)
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= 5 AND p.stockQuantity > 0 ORDER BY p.stockQuantity ASC")
    List<Product> findNearlyOutOfStockProducts(Pageable pageable);

    // Danh sách sản phẩm đã hết hàng (stock = 0)
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts(Pageable pageable);

    @Transactional
    void deleteByCategory_CategoryId(Integer categoryId);

    // Tìm danh sách sản phẩm theo danh mục
    List<Product> findByCategory(Category category);

    // Tìm danh sách sản phẩm theo thương hiệu
    List<Product> findByBrand(Brand brand);

    // Xóa tất cả sản phẩm theo categoryId
    @Modifying
    @Query("DELETE FROM Product p WHERE p.category.categoryId = :categoryId")
    void deleteByCategory_CategoryId(@Param("categoryId") int categoryId);


}