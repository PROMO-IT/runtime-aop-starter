package ru.promoit.invoke;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.promoit.aspect.AfterAspect;
import ru.promoit.aspect.Aspect;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.aspect.OverrideAspect;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

public class AspectInvoker implements MethodInterceptor {
    private final Class<?> clazz;
    private final String methodName;
    private final Supplier<Aspect> aspect;

    public AspectInvoker(Class<?> clazz, String methodName, Supplier<Aspect> aspect) {
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
        Aspect aspect = this.aspect.get();

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
                .orElse(args);

        Object result = method.invoke(obj, targs);

        return Optional.ofNullable(aspect)
                .filter(a -> a instanceof AfterAspect)
                .map(a -> (AfterAspect) a)
                .map(a -> a.afterAdvice(obj, args, result))
                .orElse(result);
    }
}
