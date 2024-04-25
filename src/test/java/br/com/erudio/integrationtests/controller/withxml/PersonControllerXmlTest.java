package br.com.erudio.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.erudio.integrationtests.vo.wrappers.WrapperPersonVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new XmlMapper();
        //Configura comportamento ao receber o JSON para ignorar campos HATEOAS
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        person = new PersonVO();
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(person)
                .when()
                .put()
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
        assertTrue(persistePerson.getEnabled());

        assertEquals(person.getId(), persistePerson.getId());

        assertEquals("Nelson", persistePerson.getFirstName());
        assertEquals("Piquet Souto", persistePerson.getLastName());
        assertEquals("Brasilia - DF Brasil", persistePerson.getAddress());
        assertEquals("Male", persistePerson.getGender());
    }

    @Test
    @Order(3)
    public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParams("id", person.getId())
                .when()
                .patch("{id}")
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParams("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

    }

    @Test
    @Order(6)
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

        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
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
    @Order(7)
    public void testFindByName() throws JsonMappingException, JsonProcessingException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("firstName", "ikol")
                .queryParams("page",0, "size", 10, "directions", "asc")
                .when()
                .get("findPersonByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        // Como o restassured usa uma abstração sobre objectmapper do Jackson ocorre um erro
        // Convertemos para string para melhor realização dos testes

        PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
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
    @Order(8)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
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

    @Test
    @Order(9)
    public void testHATEOAS() throws JsonMappingException, JsonProcessingException {

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

        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/207</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/663</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/11</href></links>"));

        assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=0&amp;size=10&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=2&amp;size=10&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1?page=3&amp;size=10&amp;direction=asc</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=4&amp;size=10&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=100&amp;size=10&amp;sort=firstName,asc</href></links>"));

        //assertTrue(content.contains("<page><size>10</size><totalElements>1009</totalElements><totalPages>101</totalPages><number>3</number></page>"));
    }

    private void mockPerson() {
        person.setFirstName("Nelson");
        person.setLastName("Piquet");
        person.setAddress("Brasilia - DF Brasil");
        person.setGender("Male");
        person.setEnabled(true);
    }

}
