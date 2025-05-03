package com.fit.tax;

import com.fit.tax.context.TaxContext;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaxCalculationDefinitionTest {

    final TaxCalculationDefinition taxCalculation = new TaxCalculationDefinition();
    final Percentage offset = Percentage.withPercentage(0.1);

    @Test
    void exercice_0() {
        TaxContext taxContext = new TaxContext(10000);

        taxCalculation.execute(taxContext);
        assertThat(taxContext.getTax()).isCloseTo(0.0, offset);
    }

    @Test
    void exercice_1() {
        TaxContext taxContext = new TaxContext(20000);

        taxCalculation.execute(taxContext);
        assertThat(taxContext.getTax()).isCloseTo(1800, offset);
    }

    @Test
    void exercice_2() {
        TaxContext taxContext = new TaxContext(31000);

        taxCalculation.execute(taxContext);
        assertThat(taxContext.getTax()).isCloseTo(3348, offset);
    }

    @Test
    void exercice_3() {
        TaxContext taxContext = new TaxContext(41000);

        taxCalculation.execute(taxContext);
        assertThat(taxContext.getTax()).isCloseTo(4428, offset);
    }

    @Test
    void exercice_4() {
        TaxContext taxContext = new TaxContext(90000);

        taxCalculation.execute(taxContext);
        assertThat(taxContext.getTax()).isCloseTo(12150, offset);
    }

    @Test
    void exercice_5() {
        TaxContext taxContext = new TaxContext(200000);

        taxCalculation.execute(taxContext);
        assertThat(taxContext.getTax()).isCloseTo(36000, offset);
    }

}