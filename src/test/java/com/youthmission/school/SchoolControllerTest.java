package com.youthmission.school;

import com.youthmission.WithAccount;
import com.youthmission.account.AccountRepository;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class studentrollerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired SchoolService schoolService;
    @Autowired SchoolRepository schoolRepository;
    @Autowired AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("joshua8565@gmail.com")
    @DisplayName("교회학교 개설 폼 조회")
    void createSchoolForm() throws Exception {
        mockMvc.perform(get("/new-school"))
                .andExpect(status().isOk())
                .andExpect(view().name("school/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("schoolForm"));
    }

    @Test
    @WithAccount("joshua8565@gmail.com")
    @DisplayName("교회학교 개설 - 완료")
    void createSchool_success() throws Exception {
        mockMvc.perform(post("/new-school")
                .param("path", "test-path")
                .param("title", "school title")
                .param("shortDescription", "short description of a school")
                .param("fullDescription", "full description of a school")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/school/test-path"));

        School school = schoolRepository.findByPath("test-path");
        assertNotNull(school);
        Account account = accountRepository.findByEmail("joshua8565@gmail.com");
        assertTrue(school.getManagers().contains(account));
    }

    @Test
    @WithAccount("joshua8565@gmail.com")
    @DisplayName("교회학교 개설 - 실패")
    void createSchool_fail() throws Exception {
        mockMvc.perform(post("/new-school")
                .param("path", "wrong path")
                .param("title", "school title")
                .param("shortDescription", "short description of a school")
                .param("fullDescription", "full description of a school")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("school/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("schoolForm"))
                .andExpect(model().attributeExists("account"));

        School school = schoolRepository.findByPath("test-path");
        assertNull(school);
    }

    @Test
    @WithAccount("joshua8565@gmail.com")
    @DisplayName("교회학교 조회")
    void viewSchool() throws Exception {
        School school = new School();
        school.setPath("test-path");
        school.setTitle("test school");
        school.setShortDescription("short description");
        school.setFullDescription("<p>full description</p>");

        Account joshua8565 = accountRepository.findByEmail("joshua8565@gmail.com");
        schoolService.createNewSchool(school, joshua8565);

        mockMvc.perform(get("/school/test-path"))
                .andExpect(view().name("school/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("school"));
    }
}