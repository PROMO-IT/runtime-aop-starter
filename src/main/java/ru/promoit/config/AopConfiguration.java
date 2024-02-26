package ru.promoit.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.bpp.AspectInvokeBeanPostProcessor;
import ru.promoit.component.TestComponent1;
import ru.promoit.component.TestComponent2;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.loader.GroovyAspectClassLoader;
import ru.promoit.loader.GroovyAspectFileLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AopConfiguration {
    @Bean
    public BeanPostProcessor aspectInvokeBpp() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        GroovyAspectClassLoader<BeforeAspect> loader1 = new GroovyAspectClassLoader<>("ru.promoit.component.BeforeAspect1");
        GroovyAspectFileLoader<BeforeAspect> loader2 = new GroovyAspectFileLoader<>("src/main/resources/BeforeAspect2.groovy");
        List<AspectInvoker> invokers = Arrays.asList(
                new AspectInvoker<>(TestComponent1.class, "testMethod1", loader1.load(), null, null),
                new AspectInvoker<>(TestComponent2.class, "testMethod3", loader2.load(), null, null)
        );

        return new AspectInvokeBeanPostProcessor(invokers);
    }
}
