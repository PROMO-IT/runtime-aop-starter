package ru.promoit.aspect;

public interface AfterAspect<T> {
    default Object afterAdvice(T obj, Object[] args, Object result) throws Throwable {
        return result;
    }
}
