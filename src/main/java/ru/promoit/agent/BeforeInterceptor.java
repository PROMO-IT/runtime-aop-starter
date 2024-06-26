package ru.promoit.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import ru.promoit.aspect.BeforeAspect;

import java.lang.reflect.Method;
import java.util.Objects;

public class BeforeInterceptor extends AbstractInterceptor {

    public BeforeInterceptor(String clazz, String methodName) {
        super(clazz, methodName);
    }

    @RuntimeType
    @Override
    public Object intercept(@This Object object, @Origin Method method, @Morph Morpher m, @AllArguments Object[] args) throws Exception {
        BeforeAspect beforeAspect = (BeforeAspect) aspect.get();
        Object[] targs = beforeAspect.beforeAdvice(object, args, beanFactory);
        if (Objects.isNull(targs)) {
            targs = args;
        }
        Object result = Objects.isNull(m) ? method.invoke(object, targs) : m.invoke(targs);
        return result;
    }
}
