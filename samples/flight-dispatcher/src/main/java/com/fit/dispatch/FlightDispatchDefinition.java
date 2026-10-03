package com.fit.dispatch;

import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import com.fit.dispatch.context.DispatchContext;
import com.fit.dispatch.rules.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Predicate;

/**
 * Dispatch flow of a flight: the safety checks first, then the fuel plan and the takeoff weight check
 * when the flight is still dispatchable. Declared as a CDI bean, it is registered in the
 * {@link com.fit.bru.quarkus.BusinessRuleEngine} by the starter.
 */
@ApplicationScoped
public class FlightDispatchDefinition extends RuleDefinition<DispatchContext> {

    public static final String NAME = "Flight Dispatch";

    @Override
    public RuleElement<DispatchContext> get() {
        Predicate<DispatchContext> dispatchable = DispatchContext::isDispatchable;

        return createRuleflow()
                .withName(NAME)
                .startWith(new RAircraftLimits().get())
                .and(new RCrewDuty().get())
                .and(new RDestinationWeather().get())
                .and(new RWeatherMinimums().get())
                .and(new RCrosswind().get())
                .andIf(dispatchable, new RFixedFuel().get())
                .andIf(dispatchable, new RContingencyFuel().get())
                .andIf(dispatchable, new RAlternateFuel().get())
                .andIf(dispatchable, new RBlockFuel().get())
                .andIf(dispatchable, new RTakeoffWeight().get())
                .build();
    }
}
