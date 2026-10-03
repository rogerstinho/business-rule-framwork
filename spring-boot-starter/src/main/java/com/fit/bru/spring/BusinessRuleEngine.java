package com.fit.bru.spring;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.DescribableRuleElement;
import com.fit.bru.rule.context.NamedRuleElement;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;
import com.fit.bru.rule.model.RuleElementModel;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;

/**
 * Entry point to execute the {@link RuleDefinition} beans of the application context.
 * <p>
 * Each definition is built once, when the engine is created, so that an invalid rule
 * (missing decision table, malformed table, ...) makes the application fail at startup.
 * The built {@link RuleElement} is then reused for every execution.
 * <p>
 * A definition can be executed by its type, by its bean name, or by the name of the built
 * {@link NamedRuleElement} (rule or rule flow name).
 */
public class BusinessRuleEngine {

    private final List<RuleDescriptor> descriptors = new ArrayList<>();
    private final Map<Class<?>, Registration> registrationsByType = new LinkedHashMap<>();
    private final Map<String, Registration> registrationsByName = new LinkedHashMap<>();

    public BusinessRuleEngine(Map<String, RuleDefinition<?>> definitions) {
        definitions.forEach((beanName, definition) -> {
            RuleElement<?> element = requireRuleItem(definition.get(),
                    "Rule definition '%s' built no rule element".formatted(beanName));

            Class<?> definitionType = ClassUtils.getUserClass(definition);
            String name = element instanceof NamedRuleElement<?> named ? named.getName() : beanName;
            RuleElementModel model = element instanceof DescribableRuleElement describable
                    ? describable.describe() : null;
            Registration registration = new Registration(new RuleDescriptor(name,
                    model != null ? model.description() : null, beanName, RuleDescriptor.Kind.of(model),
                    definitionType, contextTypeOf(definitionType)), element, model);

            descriptors.add(registration.descriptor());
            registrationsByType.put(definitionType, registration);
            register(beanName, registration);
            register(name, registration);
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
        execute(requireRuleItem(registrationsByName.get(name),
                "No rule definition named '%s' is registered".formatted(name)), context);
    }

    /**
     * Names under which the rule definitions can be executed.
     */
    public Set<String> getRuleNames() {
        return Collections.unmodifiableSet(registrationsByName.keySet());
    }

    /**
     * Descriptions of the registered rule definitions, one per definition.
     */
    public List<RuleDescriptor> getRules() {
        return Collections.unmodifiableList(descriptors);
    }

    /**
     * Finds the description of the rule definition of the given name (bean name or rule name).
     */
    public Optional<RuleDescriptor> findRule(String name) {
        return Optional.ofNullable(registrationsByName.get(name)).map(Registration::descriptor);
    }

    /**
     * Describes the execution structure of the rule definition of the given name (bean name or rule name):
     * steps of a rule flow and their entry conditions, conditions and actions of a rule, rows of a decision table.
     *
     * @return empty when no rule definition has this name, or when its rule element cannot be described
     * ({@link RuleDescriptor.Kind#CUSTOM})
     */
    public Optional<RuleElementModel> describe(String name) {
        return Optional.ofNullable(registrationsByName.get(name)).map(Registration::model);
    }

    @SuppressWarnings("unchecked")
    private <T extends RuleExecutionContext> void execute(Registration registration, T context) {
        ((RuleElement<T>) registration.element()).execute(context);
    }

    private void register(String name, Registration registration) {
        Registration existing = registrationsByName.putIfAbsent(name, registration);
        if (existing != null && existing != registration) {
            throw new RuleBuildingException(name, "Several rule definitions share the same name");
        }
    }

    private Registration findByType(Class<?> definitionType) {
        Registration registration = registrationsByType.get(definitionType);
        if (registration != null) {
            return registration;
        }
        return registrationsByType.entrySet().stream()
                .filter(entry -> definitionType.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private static Class<?> contextTypeOf(Class<?> definitionType) {
        return ResolvableType.forClass(definitionType).as(RuleDefinition.class).getGeneric(0).resolve();
    }

    private record Registration(RuleDescriptor descriptor, RuleElement<?> element, RuleElementModel model) {
    }
}
