package br.com.erudio.repositories;

import br.com.erudio.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long> {}
