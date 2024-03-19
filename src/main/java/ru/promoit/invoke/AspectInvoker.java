package ru.promoit.invoke;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import ru.promoit.agent.AbstractInterceptor;
import ru.promoit.agent.InterceptorFactory;
import ru.promoit.aspect.*;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class AspectInvoker implements MethodInterceptor {
    private final AbstractInterceptor interceptor;

    public AspectInvoker(String clazz, String methodName, Supplier<Aspect> aspect, BeanFactory beanFactory, AspectType aspectType) {
        this.interceptor = InterceptorFactory.create(aspectType, clazz, methodName, aspect, beanFactory);
    }

    public String getClazz() {
        return interceptor.getClazz();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object obj = invocation.getThis();
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();

        if (!method.getName().equals(interceptor.getMethodName())) {
            return method.invoke(obj, args);
        }

        return interceptor.intercept(obj, method, null, args);
    }
}
