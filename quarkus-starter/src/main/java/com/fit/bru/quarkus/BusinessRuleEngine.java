package com.fit.bru.quarkus;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.NamedRuleElement;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;

/**
 * Entry point to execute the {@link RuleDefinition} beans of the application.
 * <p>
 * Each definition is built once, when the engine is created, so that an invalid rule
 * (missing decision table, malformed table, ...) makes the application fail at startup.
 * The built {@link RuleElement} is then reused for every execution.
 * <p>
 * A definition can be executed by its type, by its bean name, or by the name of the built
 * {@link NamedRuleElement} (rule or rule flow name).
 */
public class BusinessRuleEngine {

    private final Map<Class<?>, RuleElement<?>> elementsByType = new LinkedHashMap<>();
    private final Map<String, RuleElement<?>> elementsByName = new LinkedHashMap<>();

    /**
     * @param definitions the rule definitions, keyed by bean name
     */
    public BusinessRuleEngine(Map<String, RuleDefinition<?>> definitions) {
        definitions.forEach((beanName, definition) -> {
            RuleElement<?> element = requireRuleItem(definition.get(),
                    "Rule definition '%s' built no rule element".formatted(beanName));

            elementsByType.put(definition.getClass(), element);
            register(beanName, element);
            if (element instanceof NamedRuleElement<?> named && !beanName.equals(named.getName())) {
                register(named.getName(), element);
            }
        });
    }

    /**
     * Executes the rule definition of the given type on the context.
     */
    public <T extends RuleExecutionContext> void execute(Class<? extends RuleDefinition<T>> definitionType, T context) {
        execute(requireRuleItem(findByType(definitionType),
                "No rule definition of type '%s' is registered".formatted(definitionType.getName())), context);
    }

    /**
     * Executes the rule definition of the given name (bean name or rule name) on the context.
     */
    public <T extends RuleExecutionContext> void execute(String name, T context) {
        execute(requireRuleItem(elementsByName.get(name),
                "No rule definition named '%s' is registered".formatted(name)), context);
    }

    /**
     * Names under which the rule definitions can be executed.
     */
    public Set<String> getRuleNames() {
        return Collections.unmodifiableSet(elementsByName.keySet());
    }

    @SuppressWarnings("unchecked")
    private <T extends RuleExecutionContext> void execute(RuleElement<?> element, T context) {
        ((RuleElement<T>) element).execute(context);
    }

    private void register(String name, RuleElement<?> element) {
        RuleElement<?> existing = elementsByName.putIfAbsent(name, element);
        if (existing != null && existing != element) {
            throw new RuleBuildingException(name, "Several rule definitions share the same name");
        }
    }

    private RuleElement<?> findByType(Class<?> definitionType) {
        RuleElement<?> element = elementsByType.get(definitionType);
        if (element != null) {
            return element;
        }
        // normal scoped beans are client proxies, i.e. subclasses of the definition type
        return elementsByType.entrySet().stream()
                .filter(entry -> definitionType.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
