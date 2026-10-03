package com.fit.bru.quarkus;

import com.fit.bru.rule.builder.RuleDefinition;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CDI configuration of the business rule framework: exposes a {@link BusinessRuleEngine}
 * holding every {@link RuleDefinition} bean of the application.
 * <p>
 * The engine is created at startup, and can be replaced by declaring another
 * {@link BusinessRuleEngine} bean. The configuration can be disabled at build time
 * with {@code bru.enabled=false}.
 */
@ApplicationScoped
@IfBuildProperty(name = "bru.enabled", stringValue = "true", enableIfMissing = true)
public class BusinessRuleProducer {

    @Produces
    @Singleton
    @Startup
    @DefaultBean
    public BusinessRuleEngine businessRuleEngine(Instance<RuleDefinition<?>> ruleDefinitions) {
        Map<String, RuleDefinition<?>> definitions = new LinkedHashMap<>();
        for (Instance.Handle<RuleDefinition<?>> handle : ruleDefinitions.handles()) {
            definitions.put(beanName(handle.getBean()), handle.get());
        }
        return new BusinessRuleEngine(definitions);
    }

    /**
     * The {@code @Named} value of the bean, or else its class name with a lowercase first letter.
     */
    private static String beanName(Bean<?> bean) {
        if (bean.getName() != null) {
            return bean.getName();
        }
        String simpleName = bean.getBeanClass().getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}
