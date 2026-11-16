package de.doday.backend;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest(properties = {
        "PORT=0",
        "CORS_ALLOWED_ORIGINS=http://localhost:5173,https://doday-frontend.onrender.com"
})
@AutoConfigureMockMvc
class ApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Test
    void taskEndpointsFollowTheJsonContract() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].title").isString())
                .andExpect(jsonPath("$[0].dueDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].priority").value("MUST_DO"))
                .andExpect(jsonPath("$[0].done").isBoolean());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().toString()));

        MvcResult created = mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content("""
                                {"title":"API-Test","dueDate":"2026-11-20","priority":"NORMAL","done":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("API-Test"))
                .andExpect(jsonPath("$.dueDate").value("2026-11-20"))
                .andExpect(jsonPath("$.priority").value("NORMAL"))
                .andExpect(jsonPath("$.done").value(true))
                .andReturn();

        String createdPath = URI.create(created.getResponse().getHeader(HttpHeaders.LOCATION)).getPath();
        String id = createdPath.substring(createdPath.lastIndexOf('/') + 1);
        mockMvc.perform(get(createdPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(id)));

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content("""
                                {"title":"Aktualisiert","dueDate":"2026-11-21","priority":"OPTIONAL","done":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Aktualisiert"))
                .andExpect(jsonPath("$.dueDate").value("2026-11-21"))
                .andExpect(jsonPath("$.priority").value("OPTIONAL"))
                .andExpect(jsonPath("$.done").value(false));

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void habitEndpointsFollowTheJsonContract() throws Exception {
        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].done").isBoolean());

        mockMvc.perform(get("/api/habits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        MvcResult created = mockMvc.perform(post("/api/habits")
                        .contentType("application/json")
                        .content("""
                                {"name":"API-Test-Gewohnheit","done":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("API-Test-Gewohnheit"))
                .andExpect(jsonPath("$.done").value(true))
                .andReturn();

        String createdPath = URI.create(created.getResponse().getHeader(HttpHeaders.LOCATION)).getPath();
        String id = createdPath.substring(createdPath.lastIndexOf('/') + 1);
        mockMvc.perform(get(createdPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(id)));

        mockMvc.perform(put("/api/habits/{id}", id)
                        .contentType("application/json")
                        .content("""
                                {"name":"Aktualisierte Gewohnheit","done":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aktualisierte Gewohnheit"))
                .andExpect(jsonPath("$.done").value(false));

        mockMvc.perform(delete("/api/habits/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void corsAllowsLocalAndConfiguredFrontendPreflightRequests() throws Exception {
        for (String origin : List.of("http://localhost:5173", "https://doday-frontend.onrender.com")) {
            mockMvc.perform(options("/api/tasks")
                            .header(HttpHeaders.ORIGIN, origin)
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin))
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsStringIgnoringCase("GET")))
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsStringIgnoringCase("POST")))
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsStringIgnoringCase("PUT")))
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsStringIgnoringCase("DELETE")))
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsStringIgnoringCase("OPTIONS")))
                    .andExpect(header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsStringIgnoringCase("content-type")));
        }
    }

    @Test
    void serverPortUsesTheRenderPortEnvironmentVariable() {
        org.assertj.core.api.Assertions.assertThat(environment.getProperty("server.port")).isEqualTo("0");
    }
}
