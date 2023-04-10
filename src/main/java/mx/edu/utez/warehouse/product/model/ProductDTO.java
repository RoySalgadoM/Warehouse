package mx.edu.utez.warehouse.product.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double unitPrice;
    private Integer quantity;
    private String unit;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String description, Double unitPrice, Integer quantity, String unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.unit = unit;
    }


}
