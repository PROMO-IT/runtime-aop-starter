package ru.promoit.invoke;


import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;

import java.util.List;

public class ProxyBuilder {
    public static <T> T build(T obj, List<MethodInterceptor> interceptors) {
        ProxyFactory pf = new ProxyFactory(obj);
        interceptors.forEach(pf::addAdvice);
        return (T) pf.getProxy();
    }
}
