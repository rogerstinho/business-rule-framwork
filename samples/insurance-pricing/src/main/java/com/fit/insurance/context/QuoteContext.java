package com.fit.insurance.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fit.bru.rule.context.RuleExecutionContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuoteContext implements RuleExecutionContext {

    private final QuoteRequest request;

    private final List<String> rejectionReasons = new ArrayList<>();
    private final Map<String, Double> factors = new LinkedHashMap<>();
    private double basePremium;
    private BigDecimal premium;

    /**
     * Delegating creator, so that the generic endpoint {@code POST /business-rules/{name}/execute}
     * of the starter builds the context from a quote request body.
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public QuoteContext(QuoteRequest request) {
        this.request = request;
    }

    public QuoteRequest getRequest() {
        return request;
    }

    public boolean isEligible() {
        return rejectionReasons.isEmpty();
    }

    public void reject(String reason) {
        rejectionReasons.add(reason);
    }

    public List<String> getRejectionReasons() {
        return Collections.unmodifiableList(rejectionReasons);
    }

    public void applyFactor(String label, double factor) {
        factors.put(label, factor);
    }

    public Map<String, Double> getFactors() {
        return Collections.unmodifiableMap(factors);
    }

    public double getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(double basePremium) {
        this.basePremium = basePremium;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }
}
