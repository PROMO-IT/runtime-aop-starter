package ru.promoit.aspect;

public interface OverrideAspect<T> {
    default Object overrideAdvice(T obj, Object[] args) throws Throwable {
        return null;
    }
}
