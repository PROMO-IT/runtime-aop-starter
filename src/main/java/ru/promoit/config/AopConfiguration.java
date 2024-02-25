package ru.promoit.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.bpp.AspectInvokeBeanPostProcessor;
import ru.promoit.component.TestComponent1;
import ru.promoit.component.TestComponent2;
import ru.promoit.invoke.AspectInvoker;

import java.util.Arrays;
import java.util.List;

@Configuration
public class AopConfiguration {
    @Bean
    public BeanPostProcessor aspectInvokeBpp(BeanFactory beanFactory) {
        BeforeAspect<TestComponent1> beforeAspect1 = new BeforeAspect<>() {
            @Override
            public Object[] beforeAdvice(TestComponent1 obj, Object[] args) throws Throwable {
                String s = (String) args[0];
                System.out.println("before1" + s);
                return args;
            }
        };

        BeforeAspect<TestComponent2> beforeAspect2 = new BeforeAspect<>() {
            @Override
            public Object[] beforeAdvice(TestComponent2 obj, Object[] args) throws Throwable {
                String s = (String) args[0];
                System.out.println("before2" + s);
                return args;
            }
        };

        List<AspectInvoker> invokers = Arrays.asList(
                new AspectInvoker<>(TestComponent1.class, "testMethod1", beforeAspect1, null, null),
                new AspectInvoker<>(TestComponent2.class, "testMethod3", beforeAspect2, null, null)
        );

        return new AspectInvokeBeanPostProcessor(invokers);
    }
}
