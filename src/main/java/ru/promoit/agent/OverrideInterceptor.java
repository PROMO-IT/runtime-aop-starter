package ru.promoit.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import ru.promoit.aspect.OverrideAspect;

import java.lang.reflect.Method;

public class OverrideInterceptor extends AbstractInterceptor {
    public OverrideInterceptor(String clazz, String methodName) {
        super(clazz, methodName);
    }

    @RuntimeType
    @Override
    public Object intercept(@This Object object, @Origin Method method, @Morph Morpher m, @AllArguments Object[] args) {
        OverrideAspect overrideAspect = (OverrideAspect) aspect.get();
        return overrideAspect.overrideAdvice(object, args, beanFactory);
    }
}
