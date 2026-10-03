package com.fit.dispatch.api;

import com.fit.bru.quarkus.BusinessRuleEngine;
import com.fit.dispatch.FlightDispatchDefinition;
import com.fit.dispatch.context.DispatchContext;
import com.fit.dispatch.context.DispatchRequest;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DispatchService {

    private final BusinessRuleEngine ruleEngine;

    public DispatchService(BusinessRuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public DispatchResponse dispatch(DispatchRequest request) {
        DispatchContext context = new DispatchContext(request);
        ruleEngine.execute(FlightDispatchDefinition.class, context);
        return DispatchResponse.of(context);
    }
}
