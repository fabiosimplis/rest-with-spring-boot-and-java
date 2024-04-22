package br.com.erudio.integrationtests.controller.withyaml;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import br.com.erudio.integrationtests.vo.wrappers.WrapperBookVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static br.com.erudio.configs.TestConfigs.ORIGIN_ERUDIO;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YMLMapper objectMapper;

    private static BookVO book;

    @BeforeAll
    public static void setup(){
        objectMapper = new YMLMapper();
        //Configura comportamento ao receber o JSON para ignorar campos HATEOAS
        book = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .config(
                        RestAssuredConfig.config()
                                .encoderConfig(EncoderConfig
                                        .encoderConfig()
                                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(user, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class, objectMapper).getAccessToken();

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

        BookVO persisteBook = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(book, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes


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

        BookVO persisteBook = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(book, objectMapper)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

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

        BookVO persisteBook = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .pathParams("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

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
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .pathParams("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

    }

    @Test
    @Order(5)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {

        var wrapper = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParams("page",3, "size", 10, "directions", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedModelBook.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

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
                 .config(RestAssuredConfig.config()
                         .encoderConfig(EncoderConfig
                                 .encoderConfig()
                                 .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                 .contentType(TestConfigs.CONTENT_TYPE_YML)
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
