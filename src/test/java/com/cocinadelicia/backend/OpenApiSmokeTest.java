// src/test/java/com/cocinadelicia/backend/OpenApiSmokeTest.java
package com.cocinadelicia.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiSmokeTest {

  @Autowired MockMvc mockMvc;

  @Test
  void swaggerUi_shouldReturn200() throws Exception {
    mockMvc.perform(get("/swagger-ui.html")).andExpect(status().isOk());
  }

  @Test
  void apiDocs_shouldReturnJson() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.openapi").exists())
        .andExpect(jsonPath("$.info.title").exists());
  }
}
