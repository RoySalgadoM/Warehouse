package mx.edu.utez.warehouse.warehouse.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDTO {
    private Long id;
    private String name;
    private String identifier;
    private Integer products;
    private Double amount;

    public WarehouseDTO() {
    }

    public WarehouseDTO(Long id, String name, String identifier, Integer products, Double amount) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.products = products;
        this.amount = amount;
    }
}
