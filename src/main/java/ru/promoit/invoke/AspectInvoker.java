package ru.promoit.invoke;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.promoit.aspect.AfterAspect;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.aspect.OverrideAspect;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Logger;

public class AspectInvoker<T> implements MethodInterceptor {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Class<T> clazz;
    private final String methodName;
    private final BeforeAspect<T> beforeAspect;
    private final AfterAspect<T> afterAspect;
    private final OverrideAspect<T> overrideAspect;

    public AspectInvoker(Class<T> clazz, String methodName, BeforeAspect<T> beforeAspect, AfterAspect<T> afterAspect, OverrideAspect<T> overrideAspect) {
        this.clazz = clazz;
        this.methodName = methodName;
        this.beforeAspect = beforeAspect;
        this.afterAspect = afterAspect;
        this.overrideAspect = overrideAspect;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        T obj = (T)invocation.getThis();
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();

        if (!method.getName().equals(methodName)) {
            return method.invoke(obj, args);
        }

        if (Objects.nonNull(overrideAspect)) {
            return overrideAspect.overrideAdvice((T)obj, args);
        }

        Object[] targs = null;
        if (Objects.nonNull(beforeAspect)) {
            try {
                targs = beforeAspect.beforeAdvice((T)obj, args);
            } catch (Throwable th) {
                log.warning("beforeAspect for " + obj.getClass().getName() + " threw: " + th.getMessage());
            }
        }

        Object result = method.invoke(obj, Objects.nonNull(targs) ? targs : args);

        if (Objects.nonNull(afterAspect)) {
            try {
                return afterAspect.afterAdvice((T)obj, args, result);
            } catch (Throwable th) {
                log.warning("afterAspect for " + obj.getClass().getName() + " threw: " + th.getMessage());
            }
        }

        return result;
    }
}
