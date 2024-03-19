package ru.promoit.agent;

import org.springframework.beans.factory.BeanFactory;
import ru.promoit.aspect.Aspect;
import ru.promoit.aspect.AspectType;

import java.util.function.Supplier;

public class InterceptorFactory {
    public static AbstractInterceptor create(AspectType type, String clazz, String methodName) {
        return switch (type) {
            case OVERRIDE -> new OverrideInterceptor(clazz, methodName);
            case BEFORE -> new BeforeInterceptor(clazz, methodName);
            case AFTER -> new AfterInterceptor(clazz, methodName);
        };
    }
    public static AbstractInterceptor create(AspectType type, String clazz, String methodName, Supplier<Aspect> aspect, BeanFactory beanFactory) {
        AbstractInterceptor interceptor = create(type, clazz, methodName);
        interceptor.setAspect(aspect);
        interceptor.setBeanFactory(beanFactory);
        return interceptor;
    }
}
