package ru.promoit.loader;

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

public class AspectLoadManager {
    private final ConfigProperties configProperties;
    private final List<GroovyAspectSourceProvider> sourceProviders;

    public AspectLoadManager(ConfigProperties configProperties, List<GroovyAspectSourceProvider> sourceProviders) {
        this.configProperties = configProperties;
        this.sourceProviders = sourceProviders;
    }

    public List<AspectInvoker> getInvokers() throws Throwable {
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

            invokers.add(new AspectInvoker(propertyMapDto.clazz, propertyMapDto.method, aspectWrapper));
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

    private PropertyMapDto parsePropertyRow(String row) throws ClassNotFoundException {
        try {
            String[] kv = row.split("=");
            String key = kv[0];
            String val = kv[1];
            String[] classMethod = key.split("#");
            String clazz = classMethod[0];
            String method = classMethod[1];
            String[] driverProp = val.split(":");
            String[] driverData = driverProp[0].split("-");
            String supplyType = driverData[0];
            String driver = driverData[1];
            String prop = driverProp[1];
            Class<?> aClass = Class.forName(clazz);
            SupplyType sType = SupplyType.valueOf(supplyType.toUpperCase());

            return new PropertyMapDto(method, driver, prop, aClass, sType);
        } catch (Exception e) {
            throw new RuntimeException("failed parsing property value", e);
        }
    }

    private record PropertyMapDto(String method, String driver, String property, Class<?> clazz, SupplyType supplyType) {}
}
