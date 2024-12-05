    package devmagic.Model;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "Product")
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int productId;

        @Column(name = "name", nullable = false)
        @NotEmpty(message = "Tên sản phẩm không được để trống")
        private String name;

        @Column(name = "description")
        private String description;

        @Column(name = "price", precision = 7, scale = 3,nullable = false)
        @NotNull(message = "Giá sản phẩm không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
        private BigDecimal price;   

        @Column(name = "sale", precision = 7, scale = 3)
        private BigDecimal sale;

        @Column(name = "stock_quantity", nullable = false)
        @Min(value = 0, message = "Số lượng tồn kho không được âm")
        private Integer stockQuantity = 0;

        @Column(name = "origin", nullable = true)
        private String origin;

        @Column(name = "unit", nullable = false)
        @NotEmpty(message = "Đơn vị không được để trống")
        private String unit;

        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private Category category;

        @ManyToOne
        @JoinColumn(name = "brand_id", nullable = false)
        private Brand brand;

        @ManyToOne
        @JoinColumn(name = "warehouse_id", nullable = false)
        @JsonBackReference
        private Warehouse warehouse;



        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ProductImage> images = new ArrayList<>();



}


