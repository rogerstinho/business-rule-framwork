package com.fit.bru.spring.web;

import com.fit.bru.exception.RuleExecutionException;
import com.fit.bru.rule.context.RuleExecutionContext;
import com.fit.bru.rule.model.RuleElementModel;
import com.fit.bru.spring.BusinessRuleEngine;
import com.fit.bru.spring.RuleDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * REST API over the {@link BusinessRuleEngine}: lists the registered rule definitions and executes them.
 * <p>
 * A rule is executed on the context sent as JSON body, which is converted to the context type of the
 * rule definition. The response is the context as left by the rule execution.
 */
@RestController
@RequestMapping("${bru.web.base-path:/business-rules}")
public class BusinessRuleController {

    private final BusinessRuleEngine ruleEngine;
    private final ObjectMapper objectMapper;

    public BusinessRuleController(BusinessRuleEngine ruleEngine, ObjectMapper objectMapper) {
        this.ruleEngine = ruleEngine;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<RuleDescriptor> rules() {
        return ruleEngine.getRules();
    }

    @GetMapping("/{name}")
    public RuleDescriptor rule(@PathVariable("name") String name) {
        return find(name);
    }

    /**
     * Execution structure of the rule definition, as a tree: a rule flow lists its steps (with their
     * entry conditions) and each step holds the rule it executes, with its conditions and actions
     * or its decision table.
     */
    @GetMapping("/{name}/flow")
    public RuleElementModel flow(@PathVariable("name") String name) {
        RuleDescriptor rule = find(name);
        return ruleEngine.describe(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "The structure of rule '%s' cannot be described".formatted(rule.name())));
    }

    @PostMapping("/{name}/execute")
    public Object execute(@PathVariable("name") String name, @RequestBody JsonNode body) {
        RuleDescriptor rule = find(name);
        RuleExecutionContext context = toContext(rule, body);
        ruleEngine.execute(rule.beanName(), context);
        return context;
    }

    @ExceptionHandler(RuleExecutionException.class)
    public ProblemDetail ruleExecutionFailed(RuleExecutionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage());
    }

    private RuleDescriptor find(String name) {
        return ruleEngine.findRule(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No rule definition named '%s' is registered".formatted(name)));
    }

    private RuleExecutionContext toContext(RuleDescriptor rule, JsonNode body) {
        Class<?> contextType = rule.contextType();
        if (contextType == null || !RuleExecutionContext.class.isAssignableFrom(contextType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The context type of rule '%s' cannot be resolved".formatted(rule.name()));
        }
        try {
            return (RuleExecutionContext) objectMapper.treeToValue(body, contextType);
        } catch (JacksonException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The body cannot be converted to '%s': %s".formatted(contextType.getName(),
                            exception.getOriginalMessage()), exception);
        }
    }
}
