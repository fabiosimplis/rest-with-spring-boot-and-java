package br.com.erudio.services;

import br.com.erudio.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonSevices {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonSevices.class.getName());

    public Person findById(String id){
        logger.info("Finding one person");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstname("Tom");
        person.setLastName("Brady");
        person.setAddress("Boston - MA");
        person.setGender("Male");

        return person;
    }

    public List<Person> findAll(){
        logger.info("Finding a list of person");
        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 8; i++){
            Person person = mockPerson(i);
            persons.add(person);
        }

        return persons;
    }

    public Person create(Person person){
        logger.info("Creating a list of person");

        return person;
    }

    public Person update(Person person){
        logger.info("Updating a list of person");

        return person;
    }

    public void delete(Long id){
        logger.info("Delete a list of person");

    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstname("Tom" + i);
        person.setLastName("Brady" + i);
        person.setAddress("Address in Boston" + i);
        person.setGender("Male");
        return person;
    }
}
