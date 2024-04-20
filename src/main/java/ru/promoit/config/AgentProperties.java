package ru.promoit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentProperties {
    private static String agentMap;

    public String agentMap() {
        return agentMap;
    }

    public static void setAgentMap(String agentMap) {
        AgentProperties.agentMap = agentMap;
    }
}
