package com.fit.bru.spring;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;
import com.fit.bru.spring.web.BusinessRuleController;
import com.fit.bru.spring.web.BusinessRuleWebAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessRuleAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(BusinessRuleAutoConfiguration.class,
                    BusinessRuleWebAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(BusinessRuleAutoConfiguration.class,
                    BusinessRuleWebAutoConfiguration.class))
            .withBean(JsonMapper.class, JsonMapper::new);

    @Test
    void engineIsCreatedWithoutRuleDefinition() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BusinessRuleEngine.class);
            assertThat(context.getBean(BusinessRuleEngine.class).getRuleNames()).isEmpty();
        });
    }

    @Test
    void ruleDefinitionsAreExecutedByTypeAndByName() {
        contextRunner.withUserConfiguration(RulesConfiguration.class).run(context -> {
            BusinessRuleEngine engine = context.getBean(BusinessRuleEngine.class);
            assertThat(engine.getRuleNames()).containsExactlyInAnyOrder("incrementRule", "Increment");

            Counter counter = new Counter();
            engine.execute(IncrementRule.class, counter);
            engine.execute("Increment", counter);
            engine.execute("incrementRule", counter);

            assertThat(counter.value).isEqualTo(3);
        });
    }

    @Test
    void ruleDefinitionsAreDescribed() {
        contextRunner.withUserConfiguration(RulesConfiguration.class).run(context -> {
            BusinessRuleEngine engine = context.getBean(BusinessRuleEngine.class);

            assertThat(engine.getRules()).containsExactly(new RuleDescriptor("Increment", null, "incrementRule",
                    RuleDescriptor.Kind.RULE, IncrementRule.class, Counter.class));
            assertThat(engine.findRule("Increment")).isEqualTo(engine.findRule("incrementRule"));
            assertThat(engine.findRule("unknown")).isEmpty();
        });
    }

    @Test
    void restApiIsExposedInServletWebApplications() {
        webContextRunner.run(context -> assertThat(context).hasSingleBean(BusinessRuleController.class));
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(BusinessRuleController.class));
    }

    @Test
    void restApiCanBeDisabled() {
        webContextRunner.withPropertyValues("bru.web.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(BusinessRuleController.class));
        webContextRunner.withPropertyValues("bru.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(BusinessRuleController.class));
    }

    @Test
    void unknownRuleNameIsRejected() {
        contextRunner.run(context -> assertThatThrownBy(() ->
                context.getBean(BusinessRuleEngine.class).execute("unknown", new Counter()))
                .isInstanceOf(RuleBuildingException.class)
                .hasMessageContaining("unknown"));
    }

    @Test
    void invalidRuleDefinitionFailsAtStartup() {
        contextRunner.withBean("invalidRule", InvalidRule.class).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).rootCause()
                    .isInstanceOf(RuleBuildingException.class)
                    .hasMessageContaining("missing-table.md");
        });
    }

    @Test
    void autoConfigurationCanBeDisabled() {
        contextRunner.withPropertyValues("bru.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(BusinessRuleEngine.class));
    }

    @Test
    void userDefinedEngineTakesPrecedence() {
        BusinessRuleEngine custom = new BusinessRuleEngine(java.util.Map.of());
        contextRunner.withBean(BusinessRuleEngine.class, () -> custom)
                .run(context -> assertThat(context.getBean(BusinessRuleEngine.class)).isSameAs(custom));
    }

    static class Counter implements RuleExecutionContext {
        int value;
    }

    static class IncrementRule extends RuleDefinition<Counter> {
        @Override
        public RuleElement<Counter> get() {
            return createRule()
                    .withName("Increment")
                    .withCondition(defaultCondition())
                    .withAction(counter -> counter.value++)
                    .build();
        }
    }

    static class InvalidRule extends RuleDefinition<Counter> {
        @Override
        public RuleElement<Counter> get() {
            return createDecisionRule()
                    .withName("Invalid")
                    .withDecisiontable("missing-table.md")
                    .withDecisionCondition(counter -> counter.value)
                    .withDecisionAction((counter, value) -> {
                    })
                    .build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class RulesConfiguration {
        @Bean
        IncrementRule incrementRule() {
            return new IncrementRule();
        }
    }
}
