package com.internetbanking.accountapi.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountControllerTest {

//    @MockBean
//    private AccountController accountController;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("Create account endpoint should work correctly with a valid request")
//    void testCreateAccountEndpoint() throws Exception {
//        CreateAccountRequest request = new CreateAccountRequest(
//                "first",
//                "last",
//                "email@email.com",
//                "28234310046",
//                "44223"
//        );
//
//        mockMvc.perform(post("/accounts/create")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @DisplayName("Login endpoint should work correctly with a valid request")
//    void testLoginEndpoint() throws Exception {
//        LoginRequest request = new LoginRequest("653742-2", "4442", "password");
//
//        mockMvc.perform(post("/accounts/login")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    @Test
//    @DisplayName("Get-data endpoint should work correctly with a valid request")
//    void testGetDataEndpoint() throws Exception {
//        UUID accountId = UUID.randomUUID();
//
//        mockMvc.perform(get("/accounts/{accountId}/get-data", accountId))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    @Test
//    @DisplayName("Update-balance endpoint should work correctly with a valid request")
//    void testUpdateBalanceEndpoint() throws Exception {
//        UpdateBalanceRequest request = new UpdateBalanceRequest();
//        request.setAccountId(UUID.randomUUID());
//        request.setAmount(BigDecimal.TEN);
//        request.setType(UpdateBalanceType.SUM);
//
//        mockMvc.perform(patch("/accounts/update-balance")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().is2xxSuccessful());
//    }
}
