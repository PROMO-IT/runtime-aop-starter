package ru.promoit.bpp;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.invoke.ProxyBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class AspectInvokeBeanPostProcessor implements BeanPostProcessor {
    private final List<AspectInvoker> invokers;

    @Lazy
    public AspectInvokeBeanPostProcessor(List<AspectInvoker> invokers) {
        this.invokers = invokers;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<MethodInterceptor> aspectInvokers = invokers.stream().filter(invoker -> bean.getClass().equals(invoker.getClazz())).collect(Collectors.toList());
        if (aspectInvokers.isEmpty()) {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
        return ProxyBuilder.build(bean, aspectInvokers);
    }
}
