package com.fit.insurance;

import com.fit.insurance.context.QuoteContext;
import com.fit.insurance.context.QuoteRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fit.insurance.context.Coverage.COMPREHENSIVE;
import static com.fit.insurance.context.Coverage.THIRD_PARTY;
import static com.fit.insurance.context.VehicleCategory.CITY;
import static com.fit.insurance.context.VehicleCategory.SEDAN;
import static com.fit.insurance.context.VehicleCategory.SPORT;
import static com.fit.insurance.rules.RClaimsHistory.FACTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class InsurancePricingDefinitionTest {

    private final InsurancePricingDefinition pricing = new InsurancePricingDefinition();

    @Test
    void experiencedDriverWithComprehensiveCoverage() {
        QuoteContext context = price(new QuoteRequest(35, 10, 0, SEDAN, 25000, COMPREHENSIVE, false, 0));

        // 680 * 1.00 (driver) * 0.90 (no claim) * 1.20 (vehicle value)
        assertThat(context.isEligible()).isTrue();
        assertThat(context.getBasePremium()).isEqualTo(680);
        assertThat(context.getFactors()).containsExactly(
                entry("Driver profile", 1.0), entry(FACTOR, 0.9), entry("Vehicle value", 1.2));
        assertThat(context.getPremium()).isEqualTo(new BigDecimal("734.40"));
    }

    @Test
    void youngDriverWithLoyaltyDiscount() {
        QuoteContext context = price(new QuoteRequest(21, 1, 1, SPORT, 40000, THIRD_PARTY, true, 0));

        // 600 * 2.00 (young driver) * 1.15 (1 claim) * 0.90 (loyalty), no vehicle value factor in third party
        assertThat(context.getFactors()).containsOnlyKeys("Driver profile", FACTOR, "Loyalty discount");
        assertThat(context.getPremium()).isEqualTo(new BigDecimal("1242.00"));
    }

    @Test
    void seniorLoyalCustomer() {
        QuoteContext context = price(new QuoteRequest(70, 45, 2, CITY, 12000, THIRD_PARTY, false, 8));

        // 280 * 1.25 (senior) * 1.40 (2 claims) * 0.90 (customer for 8 years)
        assertThat(context.getPremium()).isEqualTo(new BigDecimal("441.00"));
    }

    @Test
    void ineligibleDriverIsNotPriced() {
        QuoteContext context = price(new QuoteRequest(17, 0, 4, CITY, 8000, THIRD_PARTY, false, 0));

        assertThat(context.isEligible()).isFalse();
        assertThat(context.getRejectionReasons()).containsExactly(
                "Driver must be at least 18 years old",
                "More than 3 claims over the last 3 years");
        assertThat(context.getFactors()).isEmpty();
        assertThat(context.getPremium()).isNull();
    }

    private QuoteContext price(QuoteRequest request) {
        QuoteContext context = new QuoteContext(request);
        pricing.execute(context);
        return context;
    }
}
