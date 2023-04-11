package mx.edu.utez.warehouse.entry.service;

import mx.edu.utez.warehouse.entry.model.EntryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends JpaRepository<EntryModel, Long> {
    boolean existsById(Long id);
    EntryModel findEntryById(Long id);
}
