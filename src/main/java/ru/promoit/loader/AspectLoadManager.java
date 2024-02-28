package ru.promoit.loader;

import ru.promoit.aspect.Aspect;
import ru.promoit.config.ConfigProperties;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;

import java.util.ArrayList;
import java.util.List;

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

            Aspect aspect = switch (propertyMapDto.driver) {
                case "class" -> new GroovyAspectClassLoader(propertyMapDto.property).load();
                default -> sourceProviders.stream()
                        .filter(provider -> provider.match(propertyMapDto.driver))
                        .findFirst()
                        .map(provider -> new GroovyAspectProviderLoader(provider, propertyMapDto.property))
                        .orElseThrow(() -> new RuntimeException("no provider found for driver = " + propertyMapDto.driver))
                        .load();
            };

            invokers.add(new AspectInvoker(propertyMapDto.clazz, propertyMapDto.method, aspect));
        }

        return invokers;
    }

    private PropertyMapDto parsePropertyRow(String row) throws ClassNotFoundException {
        String[] kv = row.split("=");
        String key = kv[0];
        String val = kv[1];
        String[] classMethod = key.split("#");
        String clazz = classMethod[0];
        String method = classMethod[1];
        String[] driverProp = val.split(":");
        String driver = driverProp[0];
        String prop = driverProp[1];
        Class<?> aClass = Class.forName(clazz);

        return new PropertyMapDto(method, driver, prop, aClass);
    }

    private record PropertyMapDto(String method, String driver, String property, Class<?> clazz) {}
}
