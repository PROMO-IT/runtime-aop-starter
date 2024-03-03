package ru.promoit.aspect;

public interface AfterAspect extends Aspect {
    default Object afterAdvice(Object obj, Object[] args, Object result, Object beanFactory) throws RuntimeException {
        return result;
    }
}
