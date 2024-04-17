package br.com.erudio.controller;

import br.com.erudio.model.Person;
import br.com.erudio.services.PersonSevices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonSevices personSevices;


    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Person findById(@PathVariable(value = "id") String id) {
        return personSevices.findById(id);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> findAll() {
        return personSevices.findAll();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Person create(@RequestBody Person person) {
        return personSevices.create(person);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Person update(@RequestBody Person person) {
        return personSevices.update(person);
    }

    @RequestMapping( value = "/{id}",
            method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {

    }

}
