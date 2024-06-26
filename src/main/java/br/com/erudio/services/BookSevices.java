package br.com.erudio.services;

import br.com.erudio.controller.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookSevices {

    private Logger logger = Logger.getLogger(BookSevices.class.getName());

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    PagedResourcesAssembler<BookVO> assembler;


    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable){
        logger.info("Finding a list of book");

        var bookPages = bookRepository.findAll(pageable);
        var bookVoPages = bookPages.map(b -> DozerMapper.parseObject(b, BookVO.class));
        bookVoPages.map(bookVO -> bookVO.add(linkTo(methodOn(BookController.class).findById(bookVO.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();

        return assembler.toModel(bookVoPages, link);
    }

    public PagedModel<EntityModel<BookVO>> findBooksByTitle(String title, Pageable pageable){
        logger.info("Finding Book by title");

        var booksByTitle = bookRepository.findBooksByTitle(title, pageable);

        var bookVOPage = booksByTitle.map(b -> DozerMapper.parseObject(b, BookVO.class));
        bookVOPage.map(b -> b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();

        return assembler.toModel(bookVOPage, link);
    }

    public BookVO findById(Long id){
        logger.info("Finding one book");
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No book found for this ID!"));
        BookVO vo = DozerMapper.parseObject(book, BookVO.class);
        //criando um endereço para ele mesmo
        vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return vo;
    }

    public BookVO create(BookVO bookVO){
        logger.info("Creating a list of book");
        if (bookVO == null)
            throw new RequiredObjectIsNullException();
        Book book = DozerMapper.parseObject(bookVO, Book.class);
        BookVO vo = DozerMapper.parseObject(bookRepository.save(book), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public BookVO update(BookVO bookVO){
        logger.info("Updating a list of book");
        if (bookVO == null)
            throw new RequiredObjectIsNullException();

        Book entity = bookRepository.findById(bookVO.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No book found for this ID!"));
        entity.setAuthor(bookVO.getAuthor());
        entity.setLaunchDate(bookVO.getLaunchDate());
        entity.setPrice(bookVO.getPrice());
        entity.setTitle(bookVO.getTitle());

        var vo = DozerMapper.parseObject(bookRepository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        logger.info("Delete a list of book");
        Book entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book found for this ID!"));
        bookRepository.delete(entity);
    }
}
