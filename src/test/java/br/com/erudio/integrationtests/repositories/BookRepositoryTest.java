package br.com.erudio.integrationtests.repositories;

import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    public BookRepository repository;

    private static Book book;

    @BeforeAll
    public static void setup(){
        book = new Book();
    }

    @Test
    @Order(1)
    public void testFindByTitle() throws JsonMappingException, JsonProcessingException {

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "title"));

        Book book = repository.findBooksByTitle("avas", pageable).getContent().get(0);

        assertNotNull(book.getId());
        assertNotNull(book.getLaunchDate());
        assertNotNull(book.getPrice());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getTitle());

        assertEquals(4, book.getId());

        assertEquals(67.0, book.getPrice());
        assertEquals("Crockford", book.getAuthor());
        assertEquals("JavaScript", book.getTitle());

    }
}
