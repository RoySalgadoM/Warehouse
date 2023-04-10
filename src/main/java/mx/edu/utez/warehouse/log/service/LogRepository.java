package mx.edu.utez.warehouse.log.service;

import mx.edu.utez.warehouse.log.model.LogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogModel, Long> {
    
}
