package ru.promoit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "runtime-aop.config", value = {"aspect-map"})
@ConfigurationProperties(prefix = "runtime-aop.config")
public class ConfigProperties {
    private String aspectMap;

    public String aspectMap() {
        return aspectMap;
    }

    public void setAspectMap(String aspectMap) {
        this.aspectMap = aspectMap;
    }
}
