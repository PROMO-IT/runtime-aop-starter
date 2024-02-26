package ru.promoit.aspect;

public interface BeforeAspect extends Aspect {
    default Object[] beforeAdvice(Object obj, Object[] args) throws Throwable {
        return args;
    }
}
