package ru.promoit.agent;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

public class MyInterceptor {
    private String s;

    public MyInterceptor(String s) {
        this.s = s;
    }

    @RuntimeType
    public Object intercept(@This Object object, @Origin Method method, @AllArguments Object[] args) throws Throwable {
        System.out.println("Intercepted " + object.getClass().getName() + "#" + method.getName());
        return s;
    }
}
