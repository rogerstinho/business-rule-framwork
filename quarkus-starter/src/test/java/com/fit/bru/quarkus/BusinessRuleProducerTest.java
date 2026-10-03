package com.fit.bru.quarkus;

import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class BusinessRuleProducerTest {

    @Inject
    BusinessRuleEngine engine;

    @Test
    void ruleDefinitionBeansAreRegistered() {
        assertThat(engine.getRuleNames())
                .containsExactlyInAnyOrder("incrementRule", "Increment", "decrement", "Decrement");
    }

    @Test
    void ruleDefinitionsAreExecutedByTypeAndByName() {
        Counter counter = new Counter();
        engine.execute(IncrementRule.class, counter);
        engine.execute("Increment", counter);
        engine.execute("incrementRule", counter);
        assertThat(counter.value).isEqualTo(3);

        engine.execute(DecrementRule.class, counter);
        engine.execute("decrement", counter);
        assertThat(counter.value).isEqualTo(1);
    }

    public static class Counter implements RuleExecutionContext {
        int value;
    }

    @ApplicationScoped
    public static class IncrementRule extends RuleDefinition<Counter> {
        @Override
        public RuleElement<Counter> get() {
            return createRule()
                    .withName("Increment")
                    .withCondition(defaultCondition())
                    .withAction(counter -> counter.value++)
                    .build();
        }
    }

    @ApplicationScoped
    @Named("decrement")
    public static class DecrementRule extends RuleDefinition<Counter> {
        @Override
        public RuleElement<Counter> get() {
            return createRule()
                    .withName("Decrement")
                    .withCondition(defaultCondition())
                    .withAction(counter -> counter.value--)
                    .build();
        }
    }
}
