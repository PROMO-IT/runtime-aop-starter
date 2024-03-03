package ru.promoit.aspect;

public interface BeforeAspect extends Aspect {
    default Object[] beforeAdvice(Object obj, Object[] args, Object beanFactory) throws RuntimeException {
        return args;
    }
}
