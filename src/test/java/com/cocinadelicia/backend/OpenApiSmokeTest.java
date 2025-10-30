// src/test/java/com/cocinadelicia/backend/OpenApiSmokeTest.java
package com.cocinadelicia.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)// 👈 desactiva security filters para este test
@ActiveProfiles("test") // 👉 levanta application-test.yml
class OpenApiSmokeTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void swaggerUi_shouldRedirectToIndex() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound()) // 302
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    void apiDocs_shouldReturnJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.openapi").exists());
    }
}
