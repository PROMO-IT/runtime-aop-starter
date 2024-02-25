package ru.promoit.invoke;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class ProxyBuilder {
    public static <T> T build(T obj, MethodInterceptor interceptor) {
        T result = (T) Enhancer.create(obj.getClass(), interceptor);
        return result;
    }
}
