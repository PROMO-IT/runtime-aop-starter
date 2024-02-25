package ru.promoit.invoke;

import org.springframework.beans.factory.BeanFactory;
import ru.promoit.aspect.AfterAspect;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.aspect.OverrideAspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class AspectInvoker<T> implements InvocationHandler {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final BeanFactory beanFactory;
    private final Class<T> clazz;
    private final String methodName;
    private final BeforeAspect<T> beforeAspect;
    private final AfterAspect<T> afterAspect;
    private final OverrideAspect<T> overrideAspect;

    public AspectInvoker(BeanFactory beanFactory, Class<T> clazz, String methodName, BeforeAspect<T> beforeAspect, AfterAspect<T> afterAspect, OverrideAspect<T> overrideAspect) {
        this.beanFactory = beanFactory;
        this.clazz = clazz;
        this.methodName = methodName;
        this.beforeAspect = beforeAspect;
        this.afterAspect = afterAspect;
        this.overrideAspect = overrideAspect;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        T obj = beanFactory.getBean(clazz);
        if (!method.getName().equals(methodName)) {
            return method.invoke(obj, args);
        }

        if (Objects.nonNull(overrideAspect)) {
            return overrideAspect.overrideAdvice(obj, args);
        }

        Object[] targs = null;
        if (Objects.nonNull(beforeAspect)) {
            try {
                targs = beforeAspect.beforeAdvice(obj, args);
            } catch (Throwable th) {
                log.warning("beforeAspect for " + obj.getClass().getName() + " threw: " + th.getMessage());
            }
        }

        Object result = method.invoke(obj, Objects.nonNull(targs) ? targs : args);

        if (Objects.nonNull(afterAspect)) {
            try {
                return afterAspect.afterAdvice(obj, args, result);
            } catch (Throwable th) {
                log.warning("afterAspect for " + obj.getClass().getName() + " threw: " + th.getMessage());
            }
        }

        return result;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
