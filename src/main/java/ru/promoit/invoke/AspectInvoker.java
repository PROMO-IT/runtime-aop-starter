package ru.promoit.invoke;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.promoit.aspect.AfterAspect;
import ru.promoit.aspect.Aspect;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.aspect.OverrideAspect;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class AspectInvoker implements MethodInterceptor {
    private final Class<?> clazz;
    private final String methodName;
    private final Aspect aspect;

    public AspectInvoker(Class<?> clazz, String methodName, Aspect aspect) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.aspect = aspect;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object obj = invocation.getThis();
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();

        if (!method.getName().equals(methodName)) {
            return method.invoke(obj, args);
        }

        if (aspect instanceof OverrideAspect overrideAspect) {
            return overrideAspect.overrideAdvice(obj, args);
        }

        Object[] targs = Optional.ofNullable(aspect)
                .filter(a -> a instanceof BeforeAspect)
                .map(a -> (BeforeAspect) a)
                .map(a -> a.beforeAdvice(obj, args))
                .orElse(null);

        Object result = method.invoke(obj, Objects.nonNull(targs) ? targs : args);

        return Optional.ofNullable(aspect)
                .filter(a -> a instanceof AfterAspect)
                .map(a -> (AfterAspect) a)
                .map(a -> a.afterAdvice(obj, args, result))
                .orElse(result);
    }
}
