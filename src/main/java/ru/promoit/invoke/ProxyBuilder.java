package ru.promoit.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyBuilder {
    public static <T> T build(T obj, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), obj.getClass().getInterfaces(), handler);
    }
}
