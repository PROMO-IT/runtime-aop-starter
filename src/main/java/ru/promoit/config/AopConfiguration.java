package ru.promoit.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.promoit.bpp.AspectInvokeBeanPostProcessor;
import ru.promoit.loader.AspectLoadManager;
import ru.promoit.loader.provider.GroovyAspectFileProvider;
import ru.promoit.loader.provider.GroovyAspectJdbcProvider;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@AutoConfigureAfter(ConfigProperties.class)
@ConditionalOnBean(ConfigProperties.class)
//@EnableConfigurationProperties(ConfigProperties.class)
public class AopConfiguration {
    @Bean
//    @ConditionalOnBean(ConfigProperties.class)
    public BeanPostProcessor aspectInvokeBpp(AspectLoadManager manager) throws Throwable {
        return new AspectInvokeBeanPostProcessor(manager.getInvokers());
    }

    @Bean
    public GroovyAspectSourceProvider groovyAspectFileProvider() {
        return new GroovyAspectFileProvider();
    }

    @Bean
    @ConditionalOnBean({DataSource.class})
    public GroovyAspectSourceProvider groovyAspectJdbcProvider(DataSource dataSource) {
        return new GroovyAspectJdbcProvider(dataSource);
    }

    @Bean
//    @ConditionalOnBean(ConfigProperties.class)
    public AspectLoadManager aspectLoadManager(ConfigProperties configProperties, List<GroovyAspectSourceProvider> sourceProviders) {
        return new AspectLoadManager(configProperties, sourceProviders);
    }
}
