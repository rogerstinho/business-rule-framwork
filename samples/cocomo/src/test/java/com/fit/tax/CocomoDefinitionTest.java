package com.fit.tax;

import com.fit.bru.docs.DocumentationGenerator;
import com.fit.cocomo.CocomoDefinition;
import com.fit.cocomo.context.ProjectContext;
import com.fit.cocomo.context.SoftwareType;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CocomoDefinitionTest {

    CocomoDefinition cocomoDefinition = new CocomoDefinition();

    public static String getResourceFolder() {
        // Get the resource folder path using the ClassLoader
        return Thread.currentThread()
                .getContextClassLoader()
                .getResource("")
                .getFile();
    }

    // TODO: Fix the path to the resource folder
    @Test
    void testCocomoModelDocumentation() {
        String  folder = "C:\\Users\\roger.k.koufionou\\IdeaProjects\\fit\\business-rule-framwork";
        DocumentationGenerator.generateDocumentation(folder,"com.fit.cocomo");

    }

    @Test
    void testCocomo() {
        ProjectContext projectContext = new ProjectContext(SoftwareType.ORGANIC, 4000);

        cocomoDefinition.execute(projectContext);

        assertThat(projectContext.getEffort()).isCloseTo(10.289, Percentage.withPercentage(0.1));
        assertThat(projectContext.getDevelopmentTime()).isCloseTo(6.062, Percentage.withPercentage(0.1));
        assertThat(projectContext.getRequiredStaff()).isEqualTo(2);

    }
}