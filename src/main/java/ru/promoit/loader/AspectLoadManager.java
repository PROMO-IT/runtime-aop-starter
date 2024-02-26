package ru.promoit.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.promoit.aspect.Aspect;
import ru.promoit.aspect.BeforeAspect;
import ru.promoit.invoke.AspectInvoker;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AspectLoadManager {
    @Value("${runtime-aop.config.aspect-map}")
    private String aspectMap;

    private final List<GroovyAspectSourceProvider> sourceProviders;

    public AspectLoadManager(List<GroovyAspectSourceProvider> sourceProviders) {
        this.sourceProviders = sourceProviders;
    }

    public List<AspectInvoker> getInvokers() throws Throwable {
        String[] keyvals = aspectMap.split(";");
        List<AspectInvoker> invokers = new ArrayList<>();
        for (String keyval : keyvals) {
            String[] kv = keyval.split("=");
            String key = kv[0];
            String val = kv[1];
            String[] classMethod = key.split("#");
            String clazz = classMethod[0];
            String method = classMethod[1];
            String[] driverProp = val.split(":");
            String driver = driverProp[0];
            String prop = driverProp[1];
            Class<?> aClass = Class.forName(clazz);

            Aspect aspect = switch (driver) {
                case "class" -> new GroovyAspectClassLoader(prop).load();
                default -> sourceProviders.stream()
                        .filter(provider -> provider.match(driver))
                        .findFirst()
                        .map(provider -> new GroovyAspectProviderLoader(provider, prop)).orElseThrow().load();
            };

            invokers.add(new AspectInvoker(aClass, method, aspect));
        }

        return invokers;
    }
}
