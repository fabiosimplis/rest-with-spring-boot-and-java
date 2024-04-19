package br.com.erudio.integrationtests.controller.withjson;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integationtests.vo.PersonVO;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static br.com.erudio.configs.TestConfigs.ORIGIN_ERUDIO;
import static br.com.erudio.configs.TestConfigs.ORIGIN_SEMERU;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        //Configura comportamento ao receber o JSON para ignorar campos HATEOAS
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
    }

    @Test
    @Order(1)
    void testCreate() throws JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(person)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        PersonVO persistePerson = objectMapper.readValue(content, PersonVO.class);
        person = persistePerson;

        assertNotNull(persistePerson);
        assertNotNull(persistePerson.getId());
        assertNotNull(persistePerson.getFirstName());
        assertNotNull(persistePerson.getLastName());
        assertNotNull(persistePerson.getAddress());
        assertNotNull(persistePerson.getGender());

        assertTrue(persistePerson.getId() > 0);

        assertEquals("Richard", persistePerson.getFirstName());
        assertEquals("Stallman", persistePerson.getLastName());
        assertEquals("New York City, New York, US", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }



    @Test
    @Order(2)
    void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_SEMERU)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(person)
                        .when()
                        .post()
                        .then()
                        .statusCode(403)
                        .extract()
                        .body()
                        .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    void testFindById() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParams("id", person.getId())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        PersonVO persistePerson = objectMapper.readValue(content, PersonVO.class);
        person = persistePerson;

        assertNotNull(persistePerson);
        assertNotNull(persistePerson.getId());
        assertNotNull(persistePerson.getFirstName());
        assertNotNull(persistePerson.getLastName());
        assertNotNull(persistePerson.getAddress());
        assertNotNull(persistePerson.getGender());

        assertTrue(persistePerson.getId() > 0);

        assertEquals("Richard", persistePerson.getFirstName());
        assertEquals("Stallman", persistePerson.getLastName());
        assertEquals("New York City, New York, US", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }

    @Test
    @Order(4)
    void testFindByIdWithWrongOrigin() throws JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_SEMERU)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParams("id", person.getId())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(403)
                        .extract()
                        .body()
                        .asString();
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
    }

    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
    }

}
