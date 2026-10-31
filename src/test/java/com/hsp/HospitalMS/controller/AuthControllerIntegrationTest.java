
package com.hsp.HospitalMS.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsp.HospitalMS.entity.Role;
import com.hsp.HospitalMS.entity.RolesName;
import com.hsp.HospitalMS.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        for (RolesName roleName : RolesName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(
                        Role.builder().name(roleName).build()
                );
            }
        }
    }

    @Test
    void signup_withValidDetails_returns200() throws Exception {
        String request = """
                {
                    "name": "Integration Patient",
                    "email": "integration.patient@example.com",
                    "password": "SecurePass123"
                }
                """;

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Integration Patient"))
                .andExpect(jsonPath("$.email")
                        .value("integration.patient@example.com"));
    }

    @Test
    void signup_withDuplicateEmail_isRejected() throws Exception {
        String request = """
                {
                    "name": "Integration Patient",
                    "email": "duplicate.patient@example.com",
                    "password": "SecurePass123"
                }
                """;

        // First signup should succeed.
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        // Reusing the same email should return HTTP 409 Conflict.
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Email already exists."));
    }

    @Test
    void login_withValidCredentials_returnsJwt() throws Exception {
        String signupRequest = """
                {
                    "name": "Login Patient",
                    "email": "login.patient@example.com",
                    "password": "SecurePass123"
                }
                """;

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isOk());

        String loginRequest = """
                {
                    "email": "login.patient@example.com",
                    "password": "SecurePass123"
                }
                """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        assertTrue(json.hasNonNull("token"));
        assertFalse(json.get("token").asText().isBlank());
    }

    @Test
    void login_withWrongPassword_isRejected() throws Exception {
        String request = """
                {
                    "email": "missing.patient@example.com",
                    "password": "WrongPass123"
                }
                """;

        int status = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn()
                .getResponse()
                .getStatus();

        assertTrue(
                status >= 400,
                "Invalid credentials should not return a successful response"
        );
    }


    @Test
    void signup_withInvalidEmail_isRejected() throws Exception {
        String request = """
            {
                "name": "Invalid Patient",
                "email": "not-an-email",
                "password": "SecurePass123"
            }
            """;

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }


    @Test
    void patientWithValidJwt_canAccessOwnProfile() throws Exception {
        String signupRequest = """
            {
                "name": "JWT Test Patient",
                "email": "jwt.patient@example.com",
                "password": "SecurePass123"
            }
            """;

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isOk());

        String loginRequest = """
            {
                "email": "jwt.patient@example.com",
                "password": "SecurePass123"
            }
            """;

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        );

        String token = loginJson.get("token").asText();

        mockMvc.perform(get("/patient/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("JWT Test Patient"))
                .andExpect(jsonPath("$.email")
                        .value("jwt.patient@example.com"));
    }

    @Test
    void patientWithValidJwt_cannotAccessDoctorProfile() throws Exception {
        String signupRequest = """
            {
                "name": "Role Test Patient",
                "email": "role.patient@example.com",
                "password": "SecurePass123"
            }
            """;

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isOk());

        String loginRequest = """
            {
                "email": "role.patient@example.com",
                "password": "SecurePass123"
            }
            """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        String token = json.get("token").asText();

        mockMvc.perform(get("/doctor/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}
