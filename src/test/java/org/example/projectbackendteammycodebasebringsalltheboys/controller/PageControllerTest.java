package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.SharedWebMvcTest;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SharedWebMvcTest(PageController.class)
@Import(TestViewConfig.class)
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- GET /dashboard ---

    @Test
    @DisplayName("GET /dashboard returns dashboard view")
    void dashboard_returnsDashboardView() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    @DisplayName("GET /dashboard does not redirect")
    void dashboard_doesNotRedirect() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }

    // --- GET /admin ---

    @Test
    @DisplayName("GET /admin returns admin view")
    void adminPage_returnsAdminView() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    @DisplayName("GET /admin does not redirect")
    void adminPage_doesNotRedirect() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }
}