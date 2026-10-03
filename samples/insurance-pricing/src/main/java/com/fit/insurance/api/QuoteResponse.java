package com.fit.insurance.api;

import com.fit.insurance.context.QuoteContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record QuoteResponse(boolean eligible,
                            List<String> rejectionReasons,
                            Double basePremium,
                            Map<String, Double> factors,
                            BigDecimal premium) {

    public static QuoteResponse of(QuoteContext context) {
        if (!context.isEligible()) {
            return new QuoteResponse(false, context.getRejectionReasons(), null, Map.of(), null);
        }
        return new QuoteResponse(true, List.of(), context.getBasePremium(), context.getFactors(), context.getPremium());
    }
}
