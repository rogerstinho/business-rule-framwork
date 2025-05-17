package com.fit.bru.docs;

import com.fit.bru.annotations.EntryBusinessRule;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.RuleFlow;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.NamedRuleElement;
import com.fit.bru.rule.context.RuleElement;
import org.reflections.Reflections;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Set;

import static com.fit.bru.utils.parser.MarkdownTableParser.resourceLines;

public class DocumentationGenerator {
    private DocumentationGenerator() {
        // Prevent instantiation
    }

    public static Set<Class<?>> getClassesWithBusinessRuleAnnotation(String basePackage) {
        // Use Reflections to scan the specified package
        Reflections reflections = new Reflections(basePackage);

        // Find all classes annotated with @BusinessRule
        return reflections.getTypesAnnotatedWith(EntryBusinessRule.class);
    }

    /**
     * Generates documentation for the business rules defined in the given class.
     */
    public static void generateDocumentation(String outputFolderPath, String basePackage) {
        Objects.requireNonNull(outputFolderPath);
        Objects.requireNonNull(basePackage);

        Set<Class<?>> entryClasses = getClassesWithBusinessRuleAnnotation(basePackage);
        entryClasses.forEach(entryClass -> {
                    EntryBusinessRule entryBusinessRule = entryClass.getAnnotation(EntryBusinessRule.class);
                    String filename = entryBusinessRule.name().toLowerCase().replace(" ", "_");
                    generateClassDocumentation(entryClass, outputFolderPath, filename);
                }
        );
    }

    public static void generateClassDocumentation(Class<?> clazz, String outputFolderPath, String filename) {
        if (RuleElement.class.isAssignableFrom(clazz)) {
            try (FileWriter writer = new FileWriter(outputFolderPath + "/" + filename + ".md")) {
                if (RuleDefinition.class.isAssignableFrom(clazz)) {
                    Constructor<?> constructor = clazz.getConstructor();
                    RuleDefinition<?> ruleDefinition = (RuleDefinition<?>) constructor.newInstance();
                    documentRuleDefinition(ruleDefinition, writer);
                } else {
                    documentRuleElement((RuleElement<?>) clazz.getDeclaredConstructor().newInstance(), writer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void documentRuleDefinition(RuleDefinition<?> ruleDefinition, OutputStreamWriter writer) throws IOException {

        RuleElement<?> ruleElement = ruleDefinition.get();

//        documentRuleElement(ruleElement, writer);

        if (ruleElement instanceof Rule<?> rule) {
            documentRule(rule, writer);
        } else if (ruleElement instanceof RuleFlow<?> ruleFlowChild) {
            documentRuleFlow(ruleFlowChild, writer);
        }
    }

    private static void documentRuleFlow(RuleFlow<?> ruleFlow, OutputStreamWriter writer) throws IOException {
        documentRuleElement(ruleFlow, writer);

        for (int i = 0; i < ruleFlow.getRuletasks().size(); i++) {
            RuleElement<?> ruleElement = ruleFlow.getRuletasks().get(i);

            if (ruleElement instanceof NamedRuleElement<?> namedRuleElement) {
                writer.write("### Rule " + i + ": " + namedRuleElement.getName() + "\n");
            }

            if (ruleElement instanceof Rule<?> rule) {
                documentRule(rule, writer);
            } else if (ruleElement instanceof RuleFlow<?> ruleFlowChild) {
                documentRuleFlow(ruleFlowChild, writer);
            } else if (ruleElement instanceof RuleDefinition<?> ruleDefinition) {
                documentRuleDefinition(ruleDefinition, writer);
            }
        }
    }

    private static void documentRule(Rule<?> rule, OutputStreamWriter writer) throws IOException {
//        writer.write("## Rule: " + rule.getName() + "\n");
        writer.write("- **Execution**: " + rule.getRuleDecisionTableStrategy() + "\n");
        if (Objects.nonNull(rule.getRuleDecisionTableResource())) {
            writer.write("""
                    - **Decision Table Resource**
                    
                    """);
            for (String row : resourceLines(rule.getRuleDecisionTableResource())) {
                writer.write(row + "\n");
            }
        }

        for (int i = 0; i < rule.getRuletasks().size(); i++) {

            RuleElement<?> ruleElement = rule.getRuletasks().get(i);

            if (ruleElement instanceof Rule<?> ruleChild) {
                documentRule(ruleChild, writer);
            } else if (ruleElement instanceof RuleFlow<?> ruleFlowChild) {
                documentRuleFlow(ruleFlowChild, writer);
            } else if (ruleElement instanceof RuleDefinition<?> ruleDefinition) {
                documentRuleDefinition(ruleDefinition, writer);
            }
        }
    }


    private static void documentRuleElement(RuleElement<?> ruleElement, OutputStreamWriter writer) throws IOException {
        if (ruleElement instanceof NamedRuleElement<?> namedRuleElement) {
            writer.write("## Rule: " + namedRuleElement.getName() + "\n");
            EntryBusinessRule[] businessRules = ruleElement.getClass().getDeclaredAnnotationsByType(EntryBusinessRule.class);
            for (EntryBusinessRule businessRule : businessRules) {
                writer.write("- **Description**: " + businessRule.description() + "\n");
            }
        }
    }
}
