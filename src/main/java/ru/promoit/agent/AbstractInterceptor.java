package ru.promoit.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import org.springframework.beans.factory.BeanFactory;
import ru.promoit.aspect.Aspect;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public abstract class AbstractInterceptor {
    private final String clazz;
    private final String methodName;
    protected BeanFactory beanFactory;
    protected Supplier<Aspect> aspect;

    public AbstractInterceptor(String clazz, String methodName) {
        this.clazz = clazz;
        this.methodName = methodName;
    }

    @RuntimeType
    public abstract Object intercept(@This Object object, @Origin Method method, @Morph Morpher m, @AllArguments Object[] args) throws Throwable;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Supplier<Aspect> getAspect() {
        return aspect;
    }

    public void setAspect(Supplier<Aspect> aspect) {
        this.aspect = aspect;
    }

    public String getClazz() {
        return clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "AbstractInterceptor{" +
                "clazz=" + clazz +
                ", methodName='" + methodName + '\'' +
                ", aspect=" + aspect +
                '}';
    }
}
