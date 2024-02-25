package ru.promoit.invoke;


import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;

public class ProxyBuilder {
    public static <T> T build(T obj, MethodInterceptor interceptor) {
        ProxyFactory pf = new ProxyFactory(obj);
        pf.addAdvice(interceptor);
        return (T) pf.getProxy();
    }
}
