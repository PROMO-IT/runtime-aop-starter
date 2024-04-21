package ru.promoit.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.promoit.loader.AspectLoadManager;
import ru.promoit.loader.provider.GroovyAspectFileProvider;
import ru.promoit.loader.provider.GroovyAspectJdbcProvider;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class AopConfiguration {
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
    public AspectLoadManager aspectLoadManager(List<GroovyAspectSourceProvider> sourceProviders, BeanFactory beanFactory) {
        return new AspectLoadManager(sourceProviders, beanFactory);
    }
}
