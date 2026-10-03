package com.fit.bru.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties of the business rule framework, bound from the {@code bru.*} keys.
 */
@ConfigurationProperties(prefix = "bru")
public class BusinessRuleProperties {

    /**
     * Whether the business rule auto-configuration is enabled.
     */
    private boolean enabled = true;

    private final Web web = new Web();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Web getWeb() {
        return web;
    }

    public static class Web {

        /**
         * Whether the business rules REST API is exposed (servlet web applications only).
         */
        private boolean enabled = true;

        /**
         * Base path of the business rules REST API.
         */
        private String basePath = "/business-rules";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }
}
