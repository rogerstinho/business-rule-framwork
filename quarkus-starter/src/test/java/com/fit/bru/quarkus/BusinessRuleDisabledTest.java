package com.fit.bru.quarkus;

import io.quarkus.arc.Arc;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(BusinessRuleDisabledTest.DisabledProfile.class)
class BusinessRuleDisabledTest {

    @Test
    void engineIsNotCreatedWhenDisabled() {
        assertThat(Arc.container().instance(BusinessRuleEngine.class).isAvailable()).isFalse();
    }

    public static class DisabledProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("bru.enabled", "false");
        }
    }
}
