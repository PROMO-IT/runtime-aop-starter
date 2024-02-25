package ru.promoit.bpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.invoke.ProxyBuilder;

import java.util.List;
import java.util.Objects;

public class AspectInvokeBeanPostProcessor implements BeanPostProcessor {
    private final List<AspectInvoker> invokers;

    @Lazy
    public AspectInvokeBeanPostProcessor(List<AspectInvoker> invokers) {
        this.invokers = invokers;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        AspectInvoker aspectInvoker = invokers.stream().filter(invoker -> bean.getClass().equals(invoker.getClazz())).findFirst().orElse(null);
        if (Objects.isNull(aspectInvoker)) {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
        Object obj =  ProxyBuilder.build(bean, aspectInvoker);
        return obj;
    }
}
