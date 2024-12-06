package devmagic.Dto;

import devmagic.Model.Product;
import devmagic.Reponsitory.BrandRepository;
import devmagic.Reponsitory.CategoryRepository;
import devmagic.Reponsitory.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private double price;
    private double sale;
    private int stockQuantity;
    private int categoryId;
    private int warehouseId;
    private Integer brandId;
    private String origin;
    private String unit;

    // Constructor để truyền repositories vào
    private CategoryRepository categoryRepository;
    private BrandRepository brandRepository;
    private WarehouseRepository warehouseRepository;

    // Phương thức chuyển đổi từ ProductDTO sang Product entity
    public Product toEntity() {
        Product product = new Product();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(BigDecimal.valueOf(this.price));
        product.setSale(BigDecimal.valueOf(this.sale));
        product.setStockQuantity(this.stockQuantity);
        product.setCategory(categoryRepository.findById(this.categoryId).orElse(null));
        product.setWarehouse(warehouseRepository.findById(this.warehouseId).orElse(null));
        product.setBrand(brandRepository.findById(this.brandId).orElse(null));
        product.setOrigin(this.origin);
        product.setUnit(this.unit);
        return product;
    }
}
