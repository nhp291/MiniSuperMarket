package devmagic.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}

