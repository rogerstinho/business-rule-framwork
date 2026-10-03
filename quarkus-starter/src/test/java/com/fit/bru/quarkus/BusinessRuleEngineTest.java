package com.fit.bru.quarkus;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.quarkus.BusinessRuleProducerTest.Counter;
import com.fit.bru.quarkus.BusinessRuleProducerTest.IncrementRule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessRuleEngineTest {

    @Test
    void engineWithoutRuleDefinitionHasNoRule() {
        assertThat(new BusinessRuleEngine(Map.of()).getRuleNames()).isEmpty();
    }

    @Test
    void unknownRuleNameIsRejected() {
        BusinessRuleEngine engine = new BusinessRuleEngine(Map.of());
        assertThatThrownBy(() -> engine.execute("unknown", new Counter()))
                .isInstanceOf(RuleBuildingException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void invalidRuleDefinitionIsRejectedAtCreation() {
        assertThatThrownBy(() -> new BusinessRuleEngine(Map.of("invalidRule", new InvalidRule())))
                .isInstanceOf(RuleBuildingException.class)
                .hasMessageContaining("missing-table.md");
    }

    @Test
    void duplicatedRuleNameIsRejected() {
        Map<String, RuleDefinition<?>> definitions = new LinkedHashMap<>();
        definitions.put("first", new IncrementRule());
        definitions.put("second", new IncrementRule());
        assertThatThrownBy(() -> new BusinessRuleEngine(definitions))
                .isInstanceOf(RuleBuildingException.class)
                .hasMessageContaining("Increment");
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
}
