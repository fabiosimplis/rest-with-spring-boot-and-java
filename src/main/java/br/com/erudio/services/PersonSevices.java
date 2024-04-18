package br.com.erudio.services;

import br.com.erudio.controller.PersonController;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.data.vo.v2.PersonVOV2;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.mapper.custom.PersonMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonSevices {

    private Logger logger = Logger.getLogger(PersonSevices.class.getName());

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    public PersonVO findById(Long id){
        logger.info("Finding one person");
        Person person = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        PersonVO vo = DozerMapper.parseObject(person, PersonVO.class);
        //criando um endereço para ele mesmo
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public List<PersonVO> findAll(){
        logger.info("Finding a list of person");
        return DozerMapper.parseListObjects(personRepository.findAll(), PersonVO.class);

    }

    public PersonVO create(PersonVO personVo){
        logger.info("Creating a list of person");
        var person = DozerMapper.parseObject(personVo, Person.class);


        return DozerMapper.parseObject(personRepository.save(person), PersonVO.class);
    }

    public PersonVOV2 createV2(PersonVOV2 personVo){
        logger.info("Creating a list of person V2");

        var entity = personMapper.convertVoToEntity(personVo);
        var vo = personMapper.convertEntityToVo(personRepository.save(entity));


        return vo;
    }

    public PersonVO update(PersonVO person){
        logger.info("Updating a list of person");

        Person entity = personRepository.findById(person.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return DozerMapper.parseObject(personRepository.save(entity), PersonVO.class);
    }

    public void delete(Long id){
        logger.info("Delete a list of person");
        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        personRepository.delete(entity);
    }
}
