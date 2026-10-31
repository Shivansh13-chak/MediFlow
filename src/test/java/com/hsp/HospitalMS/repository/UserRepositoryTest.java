
package com.hsp.HospitalMS.repository;

import com.hsp.HospitalMS.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUserAndFindByEmail_returnsSavedUser() {
        User user = new User();
        user.setName("Test Patient");
        user.setEmail("repository-test@example.com");
        user.setPassword("encoded-test-password");

        userRepository.saveAndFlush(user);

        Optional<User> found =
                userRepository.findByEmail("repository-test@example.com");

        assertTrue(found.isPresent());
        assertEquals("Test Patient", found.get().getName());
        assertEquals(
                "repository-test@example.com",
                found.get().getEmail()
        );
    }
}
