package mx.edu.utez.warehouse.output.service;

import mx.edu.utez.warehouse.output.model.OutputModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputRepository extends JpaRepository<OutputModel, Long> {
    boolean existsById(Long id);
    OutputModel findOutputById(Long id);
}
