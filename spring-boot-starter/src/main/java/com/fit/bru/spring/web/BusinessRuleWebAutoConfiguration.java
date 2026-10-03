package com.fit.bru.spring.web;

import com.fit.bru.spring.BusinessRuleAutoConfiguration;
import com.fit.bru.spring.BusinessRuleEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;
import tools.jackson.databind.ObjectMapper;

/**
 * Auto-configuration of the business rules REST API, exposed in servlet web applications.
 */
@AutoConfiguration(after = BusinessRuleAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({DispatcherServlet.class, ObjectMapper.class})
@ConditionalOnBean(BusinessRuleEngine.class)
@ConditionalOnProperty(prefix = "bru.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BusinessRuleWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BusinessRuleController businessRuleController(BusinessRuleEngine ruleEngine, ObjectMapper objectMapper) {
        return new BusinessRuleController(ruleEngine, objectMapper);
    }
}
