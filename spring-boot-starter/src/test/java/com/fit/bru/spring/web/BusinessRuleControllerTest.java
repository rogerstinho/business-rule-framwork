package com.fit.bru.spring.web;

import com.fit.bru.exception.RuleExecutionException;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BusinessRuleControllerTest.TestApplication.class)
@AutoConfigureMockMvc
class BusinessRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rulesAreListed() throws Exception {
        mockMvc.perform(get("/business-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Order Discount"))
                .andExpect(jsonPath("$[0].description").value("10% discount on large orders"))
                .andExpect(jsonPath("$[0].beanName").value("orderDiscount"))
                .andExpect(jsonPath("$[0].kind").value("RULE"))
                .andExpect(jsonPath("$[0].definitionType").value(OrderDiscount.class.getName()))
                .andExpect(jsonPath("$[0].contextType").value(Order.class.getName()));
    }

    @Test
    void ruleIsFoundByRuleNameAndBeanName() throws Exception {
        mockMvc.perform(get("/business-rules/{name}", "Order Discount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beanName").value("orderDiscount"));

        mockMvc.perform(get("/business-rules/{name}", "orderDiscount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Order Discount"));
    }

    @Test
    void unknownRuleIsNotFound() throws Exception {
        mockMvc.perform(get("/business-rules/{name}", "unknown"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/business-rules/{name}/execute", "unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void ruleStructureIsDescribed() throws Exception {
        mockMvc.perform(get("/business-rules/{name}/flow", "orderDiscount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("RULE"))
                .andExpect(jsonPath("$.name").value("Order Discount"))
                .andExpect(jsonPath("$.description").value("10% discount on large orders"))
                .andExpect(jsonPath("$.executionOption").value("ALL"))
                .andExpect(jsonPath("$.conditions[0]").value("Amount >= 100"))
                .andExpect(jsonPath("$.actions[0]").value("Discount = 10% of amount"));
    }

    @Test
    void ruleFlowIsDescribedWithItsStepsAndEntryConditions() throws Exception {
        mockMvc.perform(get("/business-rules/{name}/flow", "Order Processing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("RULE_FLOW"))
                .andExpect(jsonPath("$.description").value("Prices an order"))
                .andExpect(jsonPath("$.steps.length()").value(2))
                .andExpect(jsonPath("$.steps[0].order").value(1))
                .andExpect(jsonPath("$.steps[0].entryConditions").isEmpty())
                .andExpect(jsonPath("$.steps[0].element.name").value("Order Discount"))
                .andExpect(jsonPath("$.steps[1].entryConditions[0]").value("Discount granted"))
                .andExpect(jsonPath("$.steps[1].element.type").value("RULE"))
                .andExpect(jsonPath("$.steps[1].element.name").value("Free Shipping"));
    }

    @Test
    void unknownRuleStructureIsNotFound() throws Exception {
        mockMvc.perform(get("/business-rules/{name}/flow", "unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void ruleIsExecutedOnTheContextSent() throws Exception {
        mockMvc.perform(post("/business-rules/{name}/execute", "Order Discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "amount": 250 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(250.0))
                .andExpect(jsonPath("$.discount").value(25.0));
    }

    @Test
    void invalidContextIsRejected() throws Exception {
        mockMvc.perform(post("/business-rules/{name}/execute", "Order Discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "amount": "a lot" }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ruleExecutionFailureIsUnprocessable() throws Exception {
        mockMvc.perform(post("/business-rules/{name}/execute", "failingRule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Error in executing rule named 'Failing'"));
    }

    public static class Order implements RuleExecutionContext {
        private double amount;
        private double discount;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getDiscount() {
            return discount;
        }

        public void setDiscount(double discount) {
            this.discount = discount;
        }
    }

    static class OrderDiscount extends RuleDefinition<Order> {
        @Override
        public Rule<Order> get() {
            return createRule()
                    .withName("Order Discount")
                    .withDescription("10% discount on large orders")
                    .withCondition("Amount >= 100", order -> order.getAmount() >= 100)
                    .withAction("Discount = 10% of amount", order -> order.setDiscount(order.getAmount() * 0.1))
                    .build();
        }
    }

    static class OrderProcessing extends RuleDefinition<Order> {
        @Override
        public RuleElement<Order> get() {
            return createRuleflow()
                    .withName("Order Processing")
                    .withDescription("Prices an order")
                    .startWith(new OrderDiscount().get())
                    .andIf("Discount granted", order -> order.getDiscount() > 0, createRule()
                            .withName("Free Shipping")
                            .withCondition(order -> true)
                            .withAction(order -> {
                            })
                            .build())
                    .build();
        }
    }

    static class FailingRule extends RuleDefinition<Order> {
        @Override
        public RuleElement<Order> get() {
            return createRule()
                    .withName("Failing")
                    .withCondition(order -> true)
                    .withAction(order -> {
                        throw new RuleExecutionException("Failing");
                    })
                    .build();
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
        @Bean
        OrderDiscount orderDiscount() {
            return new OrderDiscount();
        }

        @Bean
        OrderProcessing orderProcessing() {
            return new OrderProcessing();
        }

        @Bean
        FailingRule failingRule() {
            return new FailingRule();
        }
    }
}
