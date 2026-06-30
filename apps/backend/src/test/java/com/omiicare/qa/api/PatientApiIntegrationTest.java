package com.omiicare.qa.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * End-to-end API test exercising the M3 vertical: JWT login, permission-guarded patient CRUD, and
 * the FHIR read facade. Runs against H2 under the {@code test} profile with the synthetic DEMO data
 * seeded by Flyway + {@code DataInitializer}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String login() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"username\":\"demo.admin\",\"password\":\"Admin@12345\"}"))
                        .andExpect(status().isOk())
                        .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.path("data").path("accessToken").asText();
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/patients")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginThenCreateFetchAndFhirReadPatient() throws Exception {
        String token = login();
        assertThat(token).isNotBlank();

        MvcResult created =
                mockMvc.perform(
                                post("/api/v1/patients")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"firstName\":\"Grace\",\"lastName\":\"Hopper\","
                                                        + "\"dateOfBirth\":\"1906-12-09\",\"gender\":\"FEMALE\","
                                                        + "\"email\":\"grace@demo.example\"}"))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.data.mrn").exists())
                        .andReturn();
        long id =
                objectMapper
                        .readTree(created.getResponse().getContentAsString())
                        .path("data")
                        .path("id")
                        .asLong();

        mockMvc.perform(get("/api/v1/patients/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lastName").value("Hopper"));

        mockMvc.perform(
                        get("/api/v1/fhir/Patient/" + id)
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Patient"))
                .andExpect(jsonPath("$.gender").value("female"));
    }

    @Test
    void validationFailureReturnsProblemDetail() throws Exception {
        String token = login();
        mockMvc.perform(
                        post("/api/v1/patients")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"firstName\":\"\",\"lastName\":\"X\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("OMII-400"));
    }
}
