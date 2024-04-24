package br.com.erudio.integrationtests.repositories;

import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.User;
import br.com.erudio.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    public UserRepository repository;

    private static User user;

    @BeforeAll
    public static void setup(){
        user = new User();
    }

    @Test
    @Order(1)
    public void testFindByUserName() {

        User user = repository.findByUserName("leandro");

        assertNotNull(user);
        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        assertNotNull(user.getRoles());
        assertNotNull(user.getAuthorities());
        assertNotNull(user.getAuthorities());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isEnabled());
        assertTrue(user.isCredentialsNonExpired());

        assertEquals("leandro", user.getUsername());


    }
}
