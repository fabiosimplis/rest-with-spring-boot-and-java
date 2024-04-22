package br.com.erudio.integrationtests.controller.withxml;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import br.com.erudio.integrationtests.vo.wrappers.WrapperBookVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static BookVO book;

    @BeforeAll
    public static void setup(){
        objectMapper = new XmlMapper();
        //Configura comportamento ao receber o JSON para ignorar campos HATEOAS
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class).getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();
    }


    @Test
    @Order(1)
    public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        BookVO persisteBook = objectMapper.readValue(content, BookVO.class);
        book = persisteBook;

        assertNotNull(persisteBook);
        assertNotNull(persisteBook.getId());
        assertNotNull(persisteBook.getAuthor());
        assertNotNull(persisteBook.getLaunchDate());
        assertNotNull(persisteBook.getTitle());
        assertNotNull(persisteBook.getPrice());

        assertTrue(persisteBook.getId() > 0);

        assertEquals("Joshua Bloch", persisteBook.getAuthor());
        assertEquals("Java Efetivo", persisteBook.getTitle());
        assertEquals(80D, persisteBook.getPrice());

    }

    @Test
    @Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
        book.setPrice(86D);

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(book)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        BookVO persisteBook = objectMapper.readValue(content, BookVO.class);
        book = persisteBook;

        assertNotNull(persisteBook);
        assertNotNull(persisteBook.getId());
        assertNotNull(persisteBook.getAuthor());
        assertNotNull(persisteBook.getLaunchDate());
        assertNotNull(persisteBook.getTitle());
        assertNotNull(persisteBook.getPrice());

        assertEquals(book.getId(), persisteBook.getId());

        assertEquals("Joshua Bloch", persisteBook.getAuthor());
        assertEquals("Java Efetivo", persisteBook.getTitle());
        assertEquals(86D, persisteBook.getPrice());
    }

    @Test
    @Order(3)
    public void testFindById() throws JsonMappingException, JsonProcessingException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParams("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        BookVO persisteBook = objectMapper.readValue(content, BookVO.class);
        book = persisteBook;

        assertNotNull(persisteBook);
        assertNotNull(persisteBook.getId());
        assertNotNull(persisteBook.getAuthor());
        assertNotNull(persisteBook.getLaunchDate());
        assertNotNull(persisteBook.getTitle());
        assertNotNull(persisteBook.getPrice());

        assertTrue(persisteBook.getId() > 0);

        assertEquals("Joshua Bloch", persisteBook.getAuthor());
        assertEquals("Java Efetivo", persisteBook.getTitle());
        assertEquals(86D, persisteBook.getPrice());
    }

    @Test
    @Order(4)
    public void testDelete() throws JsonMappingException, JsonProcessingException {

        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParams("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

    }

    @Test
    @Order(5)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .queryParams("page",3, "size", 10, "directions", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body().asString();
                /*.as(new TypeRef<List<PersonVO>>() {
                });*/
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes
        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
        var book = wrapper.getContent();
        BookVO foundBookOne = book.get(0);

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getLaunchDate());
        assertNotNull(foundBookOne.getPrice());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getTitle());

        assertEquals(62, foundBookOne.getId());

        assertEquals(168.97, foundBookOne.getPrice());
        assertEquals("Jimmy", foundBookOne.getAuthor());
        assertEquals("After the Wedding (Efter brylluppet)", foundBookOne.getTitle());

        BookVO foundBookTwo = book.get(3);

        assertNotNull(foundBookTwo.getId());
        assertNotNull(foundBookTwo.getTitle());
        assertNotNull(foundBookTwo.getAuthor());
        assertNotNull(foundBookTwo.getPrice());


        assertEquals(841, foundBookTwo.getId());

        assertEquals("Nelson", foundBookTwo.getAuthor());
        assertEquals(92.19, foundBookTwo.getPrice());
        assertEquals("Air I Breathe, The", foundBookTwo.getTitle());

    }

    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();

         given().spec(specificationWithoutToken)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .when()
                .get()
                .then()
                .statusCode(403);
                
    }

    private void mockBook() {
        book.setAuthor("Joshua Bloch");
        book.setLaunchDate(new Date());
        book.setTitle("Java Efetivo");
        book.setPrice(80D);
    }

}
