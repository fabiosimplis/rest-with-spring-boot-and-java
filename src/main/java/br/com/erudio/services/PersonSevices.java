package br.com.erudio.services;

import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonSevices {

    private Logger logger = Logger.getLogger(PersonSevices.class.getName());

    @Autowired
    private PersonRepository personRepository;

    public Person findById(Long id){
        logger.info("Finding one person");

        return personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
    }

    public List<Person> findAll(){
        logger.info("Finding a list of person");
        return personRepository.findAll();

    }

    public Person create(Person person){
        logger.info("Creating a list of person");

        return personRepository.save(person);
    }

    public Person update(Person person){
        logger.info("Updating a list of person");

        Person entity = personRepository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        entity.setFirstname(person.getFirstname());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return personRepository.save(entity);
    }

    public void delete(Long id){
        logger.info("Delete a list of person");
        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        personRepository.delete(entity);
    }
}
