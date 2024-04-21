package ru.promoit.loader;

import org.springframework.beans.factory.BeanFactory;
import ru.promoit.agent.AbstractInterceptor;
import ru.promoit.agent.InterceptorFactory;
import ru.promoit.aspect.Aspect;
import ru.promoit.aspect.AspectType;
import ru.promoit.config.AgentProperties;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;
import ru.promoit.supplier.InstantAspectSupplier;
import ru.promoit.supplier.OnceAspectSupplier;
import ru.promoit.supplier.SupplyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AspectLoadManager {
    private static final List<AbstractInterceptor> INTERCEPTORS = new ArrayList<>();
    private static Pattern PATTERN =
            Pattern.compile("(?<advice>.*?)-(?<class>.*?)\\#(?<method>.*?)\\=(?<loadingMode>.*?)\\-(?<sourceType>.*?)\\:(?<sourceValue>.*)");
    private final List<GroovyAspectSourceProvider> sourceProviders;
    private final BeanFactory beanFactory;

    public AspectLoadManager(List<GroovyAspectSourceProvider> sourceProviders, BeanFactory beanFactory) {
        this.sourceProviders = sourceProviders;
        this.beanFactory = beanFactory;
    }

    public List<AspectInvoker> getInvokers(String property) {
        return aopProperties(property).stream()
                .map(dto -> new AspectInvoker(dto.clazz, dto.method, getAspect(dto), beanFactory, dto.aspectType))
                .collect(Collectors.toList());
    }

    public static List<AbstractInterceptor> initInterceptors(String property) {
        AgentProperties.setAgentMap(property);
        INTERCEPTORS.clear();
        aopProperties(property).stream()
                .map(dto -> InterceptorFactory.create(dto.aspectType, dto.clazz, dto.method()))
                .forEach(INTERCEPTORS::add);
        return INTERCEPTORS;
    }

    public void configInterceptors(String property) {
        if (Objects.isNull(property) || property.isBlank()) {
            return;
        }

        List<PropertyMapDto> aopProperties = aopProperties(property);
        if (INTERCEPTORS.size() == aopProperties.size()) {
            for (int i = 0; i < aopProperties.size(); i++) {
                PropertyMapDto dto = aopProperties.get(i);
                AbstractInterceptor interceptor = INTERCEPTORS.get(i);
                interceptor.aspect = getAspect(dto);
                interceptor.beanFactory = beanFactory;
            }
        }
    }

    private Supplier<Aspect> getAspect(PropertyMapDto propertyMapDto) {
        Supplier<Aspect> aspect = getNativeSupplier(propertyMapDto);
        Supplier<Aspect> aspectWrapper = switch (propertyMapDto.supplyType) {
            case EVER -> aspect;
            case ONCE -> new OnceAspectSupplier(aspect);
            case INSTANT -> new InstantAspectSupplier(aspect);
        };
        return aspectWrapper;
    }


    private Supplier<Aspect> getNativeSupplier(PropertyMapDto propertyMapDto) {
        Supplier<Aspect> aspect;

        switch (propertyMapDto.driver) {
            case "class": aspect = () -> {
                try {
                    return new GroovyAspectClassLoader(propertyMapDto.property).load();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
            break;
            default:
                var aspectSourceProvider = sourceProviders.stream()
                        .filter(provider -> provider.match(propertyMapDto.driver))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("no provider found for driver = " + propertyMapDto.driver));
                aspect = () -> {
                    try {
                        return new GroovyAspectProviderLoader(aspectSourceProvider, propertyMapDto.property).load();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };
        }

        return aspect;
    }

    public record PropertyMapDto(String method, String driver, String property, String clazz, SupplyType supplyType, AspectType aspectType) {}

    public static List<PropertyMapDto> aopProperties(String property) {
        return Arrays.stream(property.split(";"))
                .map(AspectLoadManager::parsePropertyRow)
                .collect(Collectors.toList());
    }

    private static PropertyMapDto parsePropertyRow(String row) {
        try {
            Matcher m = PATTERN.matcher(row);
            m.find();
            String advice = m.group("advice");
            String clazz = m.group("class");
            String method = m.group("method");
            String supplyType = m.group("loadingMode");
            String driver = m.group("sourceType");
            String prop = m.group("sourceValue");
            SupplyType sType = SupplyType.valueOf(supplyType.toUpperCase());
            AspectType aspectType = AspectType.valueOf(advice.toUpperCase());

            return new PropertyMapDto(method, driver, prop, clazz, sType, aspectType);
        } catch (Exception e) {
            throw new RuntimeException("failed parsing property value", e);
        }
    }
}
