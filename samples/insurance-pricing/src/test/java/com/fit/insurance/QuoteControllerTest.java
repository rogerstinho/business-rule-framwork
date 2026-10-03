package com.fit.insurance;

import com.fit.bru.spring.BusinessRuleEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRuleEngine ruleEngine;

    @Test
    void pricingDefinitionIsRegisteredByTheStarter() {
        assertThat(ruleEngine.getRuleNames()).contains(InsurancePricingDefinition.NAME);
    }

    @Test
    void quoteIsPriced() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "driverAge": 35,
                                  "yearsLicensed": 10,
                                  "claimsLastThreeYears": 0,
                                  "vehicleCategory": "SEDAN",
                                  "vehicleValue": 25000,
                                  "coverage": "COMPREHENSIVE",
                                  "multiPolicy": false,
                                  "customerSinceYears": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.basePremium").value(680.0))
                .andExpect(jsonPath("$.factors['Vehicle value']").value(1.2))
                .andExpect(jsonPath("$.premium").value(734.40));
    }

    @Test
    void quoteIsRejected() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "driverAge": 40,
                                  "yearsLicensed": 20,
                                  "claimsLastThreeYears": 5,
                                  "vehicleCategory": "SUV",
                                  "vehicleValue": 30000,
                                  "coverage": "THIRD_PARTY",
                                  "multiPolicy": false,
                                  "customerSinceYears": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(false))
                .andExpect(jsonPath("$.rejectionReasons[0]").value("More than 3 claims over the last 3 years"))
                .andExpect(jsonPath("$.premium").isEmpty());
    }
}
