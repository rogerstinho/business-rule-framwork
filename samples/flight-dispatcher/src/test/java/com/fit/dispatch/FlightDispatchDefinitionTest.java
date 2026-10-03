package com.fit.dispatch;

import com.fit.dispatch.context.DispatchContext;
import com.fit.dispatch.context.DispatchRequest;
import org.junit.jupiter.api.Test;

import static com.fit.dispatch.context.AircraftType.A320;
import static com.fit.dispatch.context.AircraftType.B737;
import static com.fit.dispatch.context.AircraftType.B787;
import static com.fit.dispatch.context.FuelComponent.ALTERNATE;
import static com.fit.dispatch.context.FuelComponent.CONTINGENCY;
import static com.fit.dispatch.context.FuelComponent.FINAL_RESERVE;
import static com.fit.dispatch.context.FuelComponent.TAXI;
import static com.fit.dispatch.context.FuelComponent.TRIP;
import static com.fit.dispatch.context.WeatherCategory.MARGINAL;
import static com.fit.dispatch.context.WeatherCategory.VMC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class FlightDispatchDefinitionTest {

    private final FlightDispatchDefinition dispatch = new FlightDispatchDefinition();

    @Test
    void shortHaulFlightInGoodWeatherIsDispatched() {
        DispatchContext context = dispatch(new DispatchRequest("FD101", A320, 150, 2000, 5000, 1500, 120,
                3000, 10000, 15, 8, 12));

        assertThat(context.isDispatchable()).isTrue();
        assertThat(context.getWeatherCategory()).isEqualTo(VMC);
        // no alternate fuel in good weather, contingency = 5% of the trip fuel
        assertThat(context.getFuel()).containsExactly(
                entry(TAXI, 200), entry(TRIP, 5000), entry(CONTINGENCY, 250), entry(FINAL_RESERVE, 1100));
        assertThat(context.getBlockFuel()).isEqualTo(6550);
        // 42600 (OEW) + 17000 (150 x 100 + 2000) + 6350 (block fuel less taxi fuel)
        assertThat(context.getTakeoffWeight()).isEqualTo(65950);
    }

    @Test
    void longHaulFlightToMarginalWeatherPlansAnAlternate() {
        DispatchContext context = dispatch(new DispatchRequest("FD202", B787, 280, 15000, 60000, 4000, 600,
                800, 3000, 20, 12, 14));

        assertThat(context.isDispatchable()).isTrue();
        assertThat(context.getWeatherCategory()).isEqualTo(MARGINAL);
        // contingency = 3% of the trip fuel for a flight of 6 hours or more
        assertThat(context.getFuel()).containsExactly(entry(TAXI, 450), entry(TRIP, 60000),
                entry(CONTINGENCY, 1800), entry(ALTERNATE, 4000), entry(FINAL_RESERVE, 2500));
        assertThat(context.getBlockFuel()).isEqualTo(68750);
        assertThat(context.getTakeoffWeight()).isEqualTo(240150);
    }

    @Test
    void overweightFlightIsNotDispatched() {
        DispatchContext context = dispatch(new DispatchRequest("FD303", B737, 189, 4000, 18000, 2000, 330,
                2500, 8000, 10, 9, 11));

        assertThat(context.isDispatchable()).isFalse();
        assertThat(context.getBlockFuel()).isEqualTo(20250);
        assertThat(context.getRejectionReasons())
                .containsExactly("Takeoff weight of 84350 kg exceeds the MTOW of 79000 kg");
    }

    @Test
    void unsafeFlightIsNotDispatchedAndNotFuelled() {
        DispatchContext context = dispatch(new DispatchRequest("FD404", A320, 120, 1000, 4000, 1200, 90,
                150, 400, 42, 14, 8));

        assertThat(context.getRejectionReasons()).containsExactly(
                "Crew flight duty period exceeds 13 hours",
                "Crew rest is below the 10 hours minimum",
                "Destination weather is below landing minimums",
                "Crosswind of 42 kt exceeds the A320 limit of 38 kt");
        assertThat(context.getFuel()).isEmpty();
        assertThat(context.getBlockFuel()).isNull();
        assertThat(context.getTakeoffWeight()).isNull();
    }

    @Test
    void limitsAreInclusive() {
        // exactly 13 hours of duty, 10 hours of rest, 38 kt of crosswind, 1000 ft and 5000 m are accepted
        DispatchContext context = dispatch(new DispatchRequest("FD505", A320, 100, 0, 3000, 1000, 60,
                1000, 5000, 38, 13, 10));

        assertThat(context.getRejectionReasons()).isEmpty();
        assertThat(context.getWeatherCategory()).isEqualTo(VMC);
    }

    private DispatchContext dispatch(DispatchRequest request) {
        DispatchContext context = new DispatchContext(request);
        dispatch.execute(context);
        return context;
    }
}
