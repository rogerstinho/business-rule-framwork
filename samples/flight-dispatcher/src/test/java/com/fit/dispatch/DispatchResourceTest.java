package com.fit.dispatch;

import com.fit.bru.quarkus.BusinessRuleEngine;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
class DispatchResourceTest {

    @Inject
    BusinessRuleEngine ruleEngine;

    @Test
    void dispatchDefinitionIsRegisteredByTheStarter() {
        assertThat(ruleEngine.getRuleNames()).contains(FlightDispatchDefinition.NAME, "flightDispatchDefinition");
    }

    @Test
    void flightIsDispatched() {
        given().contentType(JSON)
                .body("""
                        {
                          "flightNumber": "FD202",
                          "aircraftType": "B787",
                          "passengers": 280,
                          "cargoKg": 15000,
                          "tripFuelKg": 60000,
                          "alternateFuelKg": 4000,
                          "flightTimeMinutes": 600,
                          "destinationCeilingFeet": 800,
                          "destinationVisibilityMeters": 3000,
                          "crosswindKnots": 20,
                          "crewDutyHours": 12,
                          "crewRestHours": 14
                        }
                        """)
                .when().post("/api/dispatch")
                .then().statusCode(200)
                .body("flightNumber", is("FD202"))
                .body("status", is("DISPATCHED"))
                .body("rejectionReasons", empty())
                .body("weatherCategory", is("MARGINAL"))
                .body("alternateRequired", is(true))
                .body("fuel.ALTERNATE", is(4000))
                .body("blockFuel", is(68750))
                .body("takeoffWeight", is(240150))
                .body("maxTakeoffWeight", is(254000));
    }

    @Test
    void flightIsNotDispatched() {
        given().contentType(JSON)
                .body("""
                        {
                          "flightNumber": "FD404",
                          "aircraftType": "A320",
                          "passengers": 120,
                          "cargoKg": 1000,
                          "tripFuelKg": 4000,
                          "alternateFuelKg": 1200,
                          "flightTimeMinutes": 90,
                          "destinationCeilingFeet": 3000,
                          "destinationVisibilityMeters": 10000,
                          "crosswindKnots": 42,
                          "crewDutyHours": 8,
                          "crewRestHours": 12
                        }
                        """)
                .when().post("/api/dispatch")
                .then().statusCode(200)
                .body("status", is("NOT_DISPATCHED"))
                .body("rejectionReasons", contains("Crosswind of 42 kt exceeds the A320 limit of 38 kt"))
                .body("blockFuel", nullValue());
    }
}
