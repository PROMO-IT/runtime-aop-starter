package ru.promoit.aspect;

public interface BeforeAspect<T> {
    default Object[] beforeAdvice(T obj, Object[] args) throws Throwable {
        return args;
    }
}
