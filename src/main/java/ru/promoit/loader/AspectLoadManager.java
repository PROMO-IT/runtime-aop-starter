package ru.promoit.loader;

import org.springframework.beans.factory.BeanFactory;
import ru.promoit.aspect.Aspect;
import ru.promoit.config.ConfigProperties;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;
import ru.promoit.supplier.InstantAspectSupplier;
import ru.promoit.supplier.OnceAspectSupplier;
import ru.promoit.supplier.SupplyType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AspectLoadManager {
    private static Pattern PATTERN = Pattern.compile("(?<class>.*?)\\#(?<method>.*?)\\=(?<loadingMode>.*?)\\-(?<sourceType>.*?)\\:(?<sourceValue>.*)");
    private final ConfigProperties configProperties;
    private final List<GroovyAspectSourceProvider> sourceProviders;

    private final BeanFactory beanFactory;

    public AspectLoadManager(ConfigProperties configProperties, List<GroovyAspectSourceProvider> sourceProviders, BeanFactory beanFactory) {
        this.configProperties = configProperties;
        this.sourceProviders = sourceProviders;
        this.beanFactory = beanFactory;
    }

    public List<AspectInvoker> getInvokers() {
        String[] keyvals = configProperties.aspectMap().split(";");
        List<AspectInvoker> invokers = new ArrayList<>();
        for (String keyval : keyvals) {
            PropertyMapDto propertyMapDto = parsePropertyRow(keyval);

            Supplier<Aspect> aspect = getNativeSupplier(propertyMapDto);
            Supplier<Aspect> aspectWrapper = switch (propertyMapDto.supplyType) {
                case EVER -> aspect;
                case ONCE -> new OnceAspectSupplier(aspect);
                case INSTANT -> new InstantAspectSupplier(aspect);
            };

            invokers.add(new AspectInvoker(propertyMapDto.clazz, propertyMapDto.method, aspectWrapper, beanFactory));
        }

        return invokers;
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

    private record PropertyMapDto(String method, String driver, String property, Class<?> clazz, SupplyType supplyType) {}

    private PropertyMapDto parsePropertyRow(String row) {
        try {
            Matcher m = PATTERN.matcher(row);
            m.find();
            String clazz = m.group("class");
            String method = m.group("method");
            String supplyType = m.group("loadingMode");
            String driver = m.group("sourceType");
            String prop = m.group("sourceValue");
            Class<?> aClass = Class.forName(clazz);
            SupplyType sType = SupplyType.valueOf(supplyType.toUpperCase());

            return new PropertyMapDto(method, driver, prop, aClass, sType);
        } catch (Exception e) {
            throw new RuntimeException("failed parsing property value", e);
        }
    }
}
