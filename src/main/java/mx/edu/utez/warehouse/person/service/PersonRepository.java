package mx.edu.utez.warehouse.person.service;

import mx.edu.utez.warehouse.person.model.PersonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<PersonModel, Long>{
}
