package ru.promoit.aspect;

public interface OverrideAspect extends Aspect {
    default Object overrideAdvice(Object obj, Object[] args) throws RuntimeException {
        return null;
    }
}
