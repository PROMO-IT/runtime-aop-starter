package ru.promoit.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import ru.promoit.bpp.AspectInvokeBeanPostProcessor;
import ru.promoit.loader.AspectLoadManager;

@AutoConfigureAfter(AspectProperties.class)
@ConditionalOnBean(AspectProperties.class)
public class AspectConfiguration {
    @Bean
    public BeanPostProcessor aspectInvokeBpp(AspectProperties aspectProperties, AspectLoadManager aspectLoadManager) {
        return new AspectInvokeBeanPostProcessor(aspectLoadManager.getInvokers(aspectProperties.aspectMap()));
    }
}
