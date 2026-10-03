package com.fit.bru.spring;

import com.fit.bru.rule.builder.RuleDefinition;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Auto-configuration of the business rule framework: exposes a {@link BusinessRuleEngine}
 * holding every {@link RuleDefinition} bean of the application context.
 */
@AutoConfiguration
@ConditionalOnClass(RuleDefinition.class)
@ConditionalOnProperty(prefix = "bru", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(BusinessRuleProperties.class)
public class BusinessRuleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BusinessRuleEngine businessRuleEngine(ListableBeanFactory beanFactory) {
        Map<String, RuleDefinition<?>> definitions = new LinkedHashMap<>();
        beanFactory.getBeansOfType(RuleDefinition.class).forEach(definitions::put);
        return new BusinessRuleEngine(definitions);
    }
}
