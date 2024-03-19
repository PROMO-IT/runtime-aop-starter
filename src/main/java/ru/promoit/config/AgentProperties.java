package ru.promoit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "runtime-aop.config", value = {"agent-map"})
@ConfigurationProperties(prefix = "runtime-aop.config")
public class AgentProperties {
    private String agentMap;

    public String agentMap() {
        return agentMap;
    }

    public void setAgentMap(String agentMap) {
        this.agentMap = agentMap;
    }
}
