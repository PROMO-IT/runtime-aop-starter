package ru.promoit.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import ru.promoit.loader.AspectLoadManager;

@AutoConfigureAfter(AgentProperties.class)
@ConditionalOnBean(AgentProperties.class)
public class AgentConfiguration {
    @Bean
    public CommandLineRunner runner(AspectLoadManager aspectLoadManager, AgentProperties properties) {
        System.out.println("AgentProperties start config");
        return args -> aspectLoadManager.configInterceptors(properties.agentMap());
    }
}
