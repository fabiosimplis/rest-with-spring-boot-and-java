package br.com.erudio.integrationtests.controller.withyaml;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.erudio.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import java.util.List;

import static br.com.erudio.configs.TestConfigs.ORIGIN_ERUDIO;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YMLMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new YMLMapper();
        //Configura comportamento ao receber o JSON para ignorar campos HATEOAS
        //objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
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
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))//filtro para logar as requisições
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))//filtro para logar as respostas
                .build();
    }


    @Test
    @Order(1)
    public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        PersonVO persistePerson = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(person, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        //PersonVO persistePerson = objectMapper.readValue(content, PersonVO.class);

        person = persistePerson;

        assertNotNull(persistePerson);
        assertNotNull(persistePerson.getId());
        assertNotNull(persistePerson.getFirstName());
        assertNotNull(persistePerson.getLastName());
        assertNotNull(persistePerson.getAddress());
        assertNotNull(persistePerson.getGender());
        assertTrue(persistePerson.getEnabled());

        assertTrue(persistePerson.getId() > 0);

        assertEquals("Nelson", persistePerson.getFirstName());
        assertEquals("Piquet", persistePerson.getLastName());
        assertEquals("Brasilia - DF Brasil", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }

    @Test
    @Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
        person.setLastName("Piquet Souto");

        PersonVO persistePerson = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(person, objectMapper)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        person = persistePerson;

        assertNotNull(persistePerson);
        assertNotNull(persistePerson.getId());
        assertNotNull(persistePerson.getFirstName());
        assertNotNull(persistePerson.getLastName());
        assertNotNull(persistePerson.getAddress());
        assertNotNull(persistePerson.getGender());

        assertEquals(person.getId(), persistePerson.getId());

        assertEquals("Nelson", persistePerson.getFirstName());
        assertEquals("Piquet Souto", persistePerson.getLastName());
        assertEquals("Brasilia - DF Brasil", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }

    @Test
    @Order(3)
    public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {

        PersonVO persistePerson = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .pathParams("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        person = persistePerson;

        assertNotNull(persistePerson);
        assertNotNull(persistePerson.getId());
        assertNotNull(persistePerson.getFirstName());
        assertNotNull(persistePerson.getLastName());
        assertNotNull(persistePerson.getAddress());
        assertNotNull(persistePerson.getGender());
        assertFalse(persistePerson.getEnabled());

        assertTrue(persistePerson.getId() > 0);

        assertEquals("Nelson", persistePerson.getFirstName());
        assertEquals("Piquet Souto", persistePerson.getLastName());
        assertEquals("Brasilia - DF Brasil", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }

    @Test
    @Order(4)
    public void testFindById() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        PersonVO persistePerson = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .pathParams("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        person = persistePerson;

        assertNotNull(persistePerson);
        assertNotNull(persistePerson.getId());
        assertNotNull(persistePerson.getFirstName());
        assertNotNull(persistePerson.getLastName());
        assertNotNull(persistePerson.getAddress());
        assertNotNull(persistePerson.getGender());
        assertFalse(persistePerson.getEnabled());

        assertTrue(persistePerson.getId() > 0);

        assertEquals("Nelson", persistePerson.getFirstName());
        assertEquals("Piquet Souto", persistePerson.getLastName());
        assertEquals("Brasilia - DF Brasil", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }

    @Test
    @Order(5)
    public void testDelete() throws JsonMappingException, JsonProcessingException {

        given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .pathParams("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

    }

    @Test
    @Order(6)
    public void testFindByName() throws JsonMappingException, JsonProcessingException {

        var wrapper = given().spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .pathParam("firstName", "ikol")
                .queryParams("page",0, "size", 10, "directions", "asc")
                .when()
                .get("findPersonByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedModelPerson.class, objectMapper);

        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        var people = wrapper.getContent();
        PersonVO foundPersonOne = people.get(0);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());

        assertEquals(9, foundPersonOne.getId());

        assertEquals("Nikola", foundPersonOne.getFirstName());
        assertEquals("Tesla", foundPersonOne.getLastName());
        assertEquals("Smiljan - Croácia", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());
        assertTrue(foundPersonOne.getEnabled());


    }

    @Test
    @Order(7)
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
                .as(PagedModelPerson.class, objectMapper);

        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        var people = wrapper.getContent();
        PersonVO foundPersonOne = people.get(0);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());

        assertEquals(207, foundPersonOne.getId());

        assertEquals("Alie", foundPersonOne.getFirstName());
        assertEquals("Simpkins", foundPersonOne.getLastName());
        assertEquals("9613 Forster Trail", foundPersonOne.getAddress());
        assertEquals("Female", foundPersonOne.getGender());
        assertFalse(foundPersonOne.getEnabled());

        PersonVO foundPersonTwo = people.get(2);

        assertNotNull(foundPersonTwo.getId());
        assertNotNull(foundPersonTwo.getFirstName());
        assertNotNull(foundPersonTwo.getLastName());
        assertNotNull(foundPersonTwo.getAddress());
        assertNotNull(foundPersonTwo.getGender());
        assertTrue(foundPersonTwo.getEnabled());

        assertEquals(11, foundPersonTwo.getId());

        assertEquals("Alisa", foundPersonTwo.getFirstName());
        assertEquals("Szwandt", foundPersonTwo.getLastName());
        assertEquals("8 Moose Place", foundPersonTwo.getAddress());
        assertEquals("Female", foundPersonTwo.getGender());
    }

    @Test
    @Order(8)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
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

    @Test
    @Order(9)
    public void testHATEOAS() throws JsonMappingException, JsonProcessingException {

        var content = given().spec(specification)
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
                .asString();
                /*.as(new TypeRef<List<PersonVO>>() {
                });*/
        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        assertTrue(content.contains("rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/207\""));
        assertTrue(content.contains("rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/663\""));
        assertTrue(content.contains("rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/11\""));



        assertTrue(content.contains("rel: \"first\"\n  href: \"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("rel: \"prev\"\n  href: \"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("rel: \"self\"\n  href: \"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\""));
        assertTrue(content.contains("rel: \"next\"\n  href: \"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("rel: \"last\"\n  href: \"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));

        assertTrue(content.contains("page:\n" +
                "  size: 10\n" +
                "  totalElements: 1009\n" +
                "  totalPages: 101\n" +
                "  number: 3"));
    }

    private void mockPerson() {
        person.setFirstName("Nelson");
        person.setLastName("Piquet");
        person.setAddress("Brasilia - DF Brasil");
        person.setGender("Male");
        person.setEnabled(true);
    }

}
