package com.omiicare.qa.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Verifies the no-double-booking business rule (BR-APPT-001) end to end. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token() throws Exception {
        MvcResult r =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"username\":\"demo.admin\",\"password\":\"Admin@12345\"}"))
                        .andReturn();
        return objectMapper
                .readTree(r.getResponse().getContentAsString())
                .path("data")
                .path("accessToken")
                .asText();
    }

    private long firstProviderId(String token) throws Exception {
        MvcResult r =
                mockMvc.perform(get("/api/v1/providers").header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
        JsonNode content =
                objectMapper.readTree(r.getResponse().getContentAsString()).path("data").path("content");
        return content.get(0).path("id").asLong();
    }

    private long createPatient(String token) throws Exception {
        MvcResult r =
                mockMvc.perform(
                                post("/api/v1/patients")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\","
                                                    + "\"dateOfBirth\":\"1815-12-10\",\"gender\":\"FEMALE\"}"))
                        .andExpect(status().isCreated())
                        .andReturn();
        return objectMapper
                .readTree(r.getResponse().getContentAsString())
                .path("data")
                .path("id")
                .asLong();
    }

    @Test
    void overlappingAppointmentForSameProviderIsRejected() throws Exception {
        String token = token();
        long providerId = firstProviderId(token);
        long patientId = createPatient(token);

        Instant start = Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MINUTES);
        Instant end = start.plus(30, ChronoUnit.MINUTES);
        String body =
                String.format(
                        "{\"patientId\":%d,\"providerId\":%d,\"scheduledStart\":\"%s\",\"scheduledEnd\":\"%s\","
                                + "\"reason\":\"Consult\"}",
                        patientId, providerId, start, end);

        // First booking succeeds.
        mockMvc.perform(
                        post("/api/v1/appointments")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("BOOKED"));

        // Overlapping booking for the same provider is rejected by BR-APPT-001.
        Instant overlapStart = start.plus(15, ChronoUnit.MINUTES);
        Instant overlapEnd = overlapStart.plus(30, ChronoUnit.MINUTES);
        String overlapBody =
                String.format(
                        "{\"patientId\":%d,\"providerId\":%d,\"scheduledStart\":\"%s\",\"scheduledEnd\":\"%s\"}",
                        patientId, providerId, overlapStart, overlapEnd);
        mockMvc.perform(
                        post("/api/v1/appointments")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(overlapBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value("OMII-422"))
                .andExpect(jsonPath("$.ruleId").value("BR-APPT-001"));
    }
}
