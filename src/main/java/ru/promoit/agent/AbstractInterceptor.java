package ru.promoit.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import org.springframework.beans.factory.BeanFactory;
import ru.promoit.aspect.Aspect;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public abstract class AbstractInterceptor {
    public final String clazz;
    public final String methodName;
    public BeanFactory beanFactory;
    public Supplier<Aspect> aspect;

    public AbstractInterceptor(String clazz, String methodName) {
        this.clazz = clazz;
        this.methodName = methodName;
    }

    @RuntimeType
    public abstract Object intercept(@This Object object, @Origin Method method, @Morph Morpher m, @AllArguments Object[] args) throws Throwable;
}
