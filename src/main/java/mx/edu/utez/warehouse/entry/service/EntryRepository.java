package mx.edu.utez.warehouse.entry.service;

import mx.edu.utez.warehouse.entry.model.EntryModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<EntryModel, Long> {
    boolean existsById(Long id);
}
