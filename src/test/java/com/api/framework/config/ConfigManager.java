package com.api.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton configuration manager that loads environment-specific properties.
 * Reads from config.properties and supports system property overrides.
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        String env = System.getProperty("env", "qa");
        String configFile = "config-" + env + ".properties";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (is != null) {
                properties.load(is);
                log.info("Loaded configuration from: {}", configFile);
            } else {
                log.warn("Config file '{}' not found. Falling back to default config.properties", configFile);
                try (InputStream defaultIs = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                    if (defaultIs != null) {
                        properties.load(defaultIs);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load configuration file: {}", configFile, e);
            throw new RuntimeException("Could not load configuration: " + configFile, e);
        }
    }

    public String getProperty(String key) {
        // System property takes highest priority (useful for CI/CD overrides)
        String sysValue = System.getProperty(key);
        if (sysValue != null && !sysValue.isBlank()) {
            return sysValue;
        }
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            log.warn("Invalid integer for key '{}', using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    // ─── Convenience Getters ───────────────────────────────────────────────────

    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public int getConnectionTimeout() {
        return getIntProperty("connection.timeout.ms", 10000);
    }

    public int getReadTimeout() {
        return getIntProperty("read.timeout.ms", 30000);
    }

    public boolean isLoggingEnabled() {
        return getBooleanProperty("logging.enabled", true);
    }

    public String getAuthToken() {
        return getProperty("auth.token", "");
    }
}
