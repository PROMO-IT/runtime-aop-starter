package ru.promoit.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.promoit.bpp.AspectInvokeBeanPostProcessor;
import ru.promoit.loader.AspectLoadManager;

@Configuration
public class AopConfiguration {
    @Bean
    public BeanPostProcessor aspectInvokeBpp(AspectLoadManager manager) throws Throwable {
        return new AspectInvokeBeanPostProcessor(manager.getInvokers());
    }
}
