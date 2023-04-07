package mx.edu.utez.warehouse.order_status.service;

import mx.edu.utez.warehouse.order_status.model.OrderStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatusModel, Long> {
}
