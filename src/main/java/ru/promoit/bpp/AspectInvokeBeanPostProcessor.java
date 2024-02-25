package ru.promoit.bpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.invoke.ProxyBuilder;

import java.util.List;
import java.util.Objects;

public class AspectInvokeBeanPostProcessor implements BeanPostProcessor {
    private final List<AspectInvoker> invokers;

    public AspectInvokeBeanPostProcessor(List<AspectInvoker> invokers) {
        this.invokers = invokers;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        AspectInvoker aspectInvoker = invokers.stream().filter(invoker -> bean == invoker.getObj()).findFirst().orElse(null);
        if (Objects.isNull(aspectInvoker)) {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
        return ProxyBuilder.build(bean, aspectInvoker);
    }
}
