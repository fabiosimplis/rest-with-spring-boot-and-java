package br.com.erudio.services;

import br.com.erudio.controller.PersonController;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.data.vo.v2.PersonVOV2;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
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

    public List<PersonVO> findAll(){
        logger.info("Finding a list of person");

        List<PersonVO> voList = DozerMapper.parseListObjects(personRepository.findAll(), PersonVO.class);

//        for (PersonVO pVO: voList)
//            pVO.add(linkTo(methodOn(PersonController.class).findById(pVO.getKey())).withSelfRel());

        voList.forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        return voList;
    }

    public PersonVO findById(Long id){
        logger.info("Finding one person");
        Person person = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        PersonVO vo = DozerMapper.parseObject(person, PersonVO.class);
        //criando um endereço para ele mesmo
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO personVo){
        logger.info("Creating a list of person");
        if (personVo == null)
            throw new RequiredObjectIsNullException();
        var person = DozerMapper.parseObject(personVo, Person.class);
        var vo = DozerMapper.parseObject(personRepository.save(person), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVOV2 createV2(PersonVOV2 personVo){
        logger.info("Creating a list of person V2");

        var entity = personMapper.convertVoToEntity(personVo);

        return personMapper.convertEntityToVo(personRepository.save(entity));
    }

    public PersonVO update(PersonVO personVO){
        logger.info("Updating a list of person");
        if (personVO == null)
            throw new RequiredObjectIsNullException();

        Person entity = personRepository.findById(personVO.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        entity.setFirstName(personVO.getFirstName());
        entity.setLastName(personVO.getLastName());
        entity.setAddress(personVO.getAddress());
        entity.setGender(personVO.getGender());

        var vo = DozerMapper.parseObject(personRepository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        logger.info("Delete a list of person");
        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No person found for this ID!"));
        personRepository.delete(entity);
    }
}
