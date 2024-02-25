package ru.promoit.config;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.bpp.AspectInvokeBeanPostProcessor;
import ru.promoit.component.TestComponent1;
import ru.promoit.component.TestComponent2;
import ru.promoit.invoke.AspectInvoker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AopConfiguration {
    @Bean
    public BeanPostProcessor aspectInvokeBpp(BeanFactory beanFactory) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GroovyClassLoader loader = new GroovyClassLoader();
        //Class aClass = loader.parseClass(new File("src/main/java/ru/promoit/component/BeforeAspect1.groovy"));
        Class<?> aClass = loader.loadClass("ru.promoit.component.BeforeAspect1");
        BeforeAspect<TestComponent1> beforeAspect1 = (BeforeAspect<TestComponent1>) aClass.getDeclaredConstructor(null).newInstance();

        aClass = loader.loadClass("ru.promoit.component.BeforeAspect2");
        BeforeAspect<TestComponent2> beforeAspect2 = (BeforeAspect<TestComponent2>) aClass.getDeclaredConstructor(null).newInstance();

        List<AspectInvoker> invokers = Arrays.asList(
                new AspectInvoker<>(TestComponent1.class, "testMethod1", beforeAspect1, null, null),
                new AspectInvoker<>(TestComponent2.class, "testMethod3", beforeAspect2, null, null)
        );

        return new AspectInvokeBeanPostProcessor(invokers);
    }
}
