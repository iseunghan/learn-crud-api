package me.iseunghan.learncrud.Accounts;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.runner.RunWith;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testGetMapping() throws Exception {

        Account account = Account.builder()
                .name("spring1")
                .build();
        Account account2 = Account.builder()
                .name("spring2")
                .build();
        Account account3 = Account.builder()
                .name("spring3")
                .build();

        mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(account)));
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(account2)));

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(account3)));

        mockMvc.perform(get("/accounts")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idx").value(1))
                .andExpect(jsonPath("$[0].name").value("spring1"))
                .andExpect(jsonPath("$[1].idx").value(2))
                .andExpect(jsonPath("$[1].name").value("spring2"))
                .andExpect(jsonPath("$[2].idx").value(3))
                .andExpect(jsonPath("$[2].name").value("spring3"))
        ;


    }

    @Test
    public void testPostMapping() throws Exception {

        Account account = Account.builder()
                .name("spring")
                .build();

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("name").value("spring"))
                ;
    }


}