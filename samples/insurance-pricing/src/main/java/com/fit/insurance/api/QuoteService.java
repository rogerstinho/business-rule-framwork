package com.fit.insurance.api;

import com.fit.bru.spring.BusinessRuleEngine;
import com.fit.insurance.InsurancePricingDefinition;
import com.fit.insurance.context.QuoteContext;
import com.fit.insurance.context.QuoteRequest;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final BusinessRuleEngine ruleEngine;

    public QuoteService(BusinessRuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public QuoteResponse quote(QuoteRequest request) {
        QuoteContext context = new QuoteContext(request);
        ruleEngine.execute(InsurancePricingDefinition.class, context);
        return QuoteResponse.of(context);
    }
}
