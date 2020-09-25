package me.iseunghan.learncrud.Accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    ModelMapper modelMapper;

    //================= GET ===============================

    @Test
    @Description("30개의 account를 페이지당 10개씩 1페이지를 조회하는 테스트")
    public void queryAccounts() throws Exception {
        // Given (0-30까지 저장)
        IntStream.range(0, 30).forEach(e ->{
            this.generatedAccount(e);
        });

        // When & Then
        mockMvc.perform(get("/accounts")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.accountList[0]._links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("page.number").value(1))
                .andExpect(jsonPath("page.size").value(10))
                ;
    }

    @Test
    @Description("Get : findOne(id) 200 응답 받기")
    public void queryOneAccount() throws Exception {
        // Given
        Account account = this.generatedAccount(100);

        // When & Then
        this.mockMvc.perform(get("/accounts/{id}", account.getIdx()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value("account100"))
                    .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @Description("Get : 존재하지 않는 account 조회_404 NotFound 응답 받기")
    public void queryOneAccount404_NotFound() throws  Exception{
        // Given

        // When & Then
        this.mockMvc.perform(get("/account/11888"))
                        .andDo(print())
                        .andExpect(status().isNotFound());
    }

    //============================ POST ===============================

    @Test
    @Description("POST : 이벤트를 성공적으로 저장201_응답 받기")
    public void insertAccount201() throws Exception {
        // Given
        AccountDTO accountDTO = AccountDTO.builder()
                .name("insertAccount")
                .build();

        // When & Then
        this.mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("insertAccount"));
    }

    @Test
    @Description("POST : 이미 존재하는 이벤트_중복일 경우400_BadRequest")
    public void insertAccount400_Duplicate() throws Exception {
        // Given
        Account account = generatedAccount(100);
        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        // When & Then
        this.mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @Description("POST : 비어있는 값을 insert할때,400 응답 받기 ")
    public void insertAccount400_Empty() throws Exception {
        // Given
        AccountDTO accountDTO = AccountDTO.builder().build();

        // When & Then
        this.mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    //============================ PUT ===============================

    @Test
    @Description("Put : 정상적으로 account 수정_200 응답 받기")
    public void updateAccount200_OK() throws  Exception {
        // Given
        Account account = generatedAccount(100);
        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);
        accountDTO.setName("update Name");

        // When & Then
        mockMvc.perform(put("/accounts/{id}", account.getIdx())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("name").value("update Name"));
    }

    @Test
    @Description("Put : 존재하지 않는 account 수정_404_응답 받기")
    public void updateAccount404_NotFound() throws  Exception {
        // Given
        Account account = generatedAccount(100);
        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        // When & Then
        mockMvc.perform(put("/accounts/11833")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Description("Put : 비어있는 값으로 수정 할때_400 응답 받기")
    public void updateAccount400_Empty() throws  Exception {
        // Given
        Account account = generatedAccount(100);
        AccountDTO accountDTO = AccountDTO.builder().build();

        // When & Then
        mockMvc.perform(put("/accounts/{id}", account.getIdx())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //============================ DELETE ===============================

    @Test
    @Description("Delete : 해당하는 id Account 정상적으로 삭제_200 응답 받기")
    public void testDeleteMapping200_OK() throws Exception {
        // Given
        Account account = this.generatedAccount(100);
        Account account1 = this.generatedAccount(200);


        // When & Then
        mockMvc.perform(delete("/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Description("Delete : 존재하지 않는 account 삭제404_NotFound 응답 받기")
    public void testDeleteMapping404_NotFound() throws Exception {
        // Given

        // When & Then
        this.mockMvc.perform(delete("/account/118332"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
    }


    @Description("Account 만들어 주는 메소드")
    private Account generatedAccount(Integer idx) {
        Account account = Account.builder()
                .name("account" + idx)
                .build();
        return this.accountService.join(account);
    }
}