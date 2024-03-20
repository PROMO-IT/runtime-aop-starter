package ru.promoit.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import ru.promoit.aspect.AfterAspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

public class AfterInterceptor extends AbstractInterceptor {
    public AfterInterceptor(String clazz, String methodName) {
        super(clazz, methodName);
    }

    @RuntimeType
    @Override
    public Object intercept(@This Object object, @Origin Method method, @Morph Morpher m, @AllArguments Object[] args) throws Exception {
        AfterAspect afterAspect = (AfterAspect) aspect.get();
        Object result = Objects.isNull(m) ? method.invoke(object, args) : m.invoke(args);
        Object tresult = afterAspect.afterAdvice(object, args, result, beanFactory);
        return Objects.isNull(tresult) ? result : tresult;
    }
}
