package ru.promoit.loader;

import groovy.lang.GroovyClassLoader;
import ru.promoit.aspect.Aspect;
import ru.promoit.loader.provider.GroovyAspectSourceProvider;

public class GroovyAspectProviderLoader<T extends Aspect> implements AspectSourceLoader<T> {
    private final GroovyAspectSourceProvider provider;
    private final String property;

    public GroovyAspectProviderLoader(GroovyAspectSourceProvider provider, String property) {
        this.provider = provider;
        this.property = property;
    }

    @Override
    public T load() throws Throwable {
        Class<?> aClass;
        try (GroovyClassLoader loader = new GroovyClassLoader()) {
            aClass = loader.parseClass(provider.provide(property));
        }

        return (T) aClass.getDeclaredConstructor(null).newInstance();
    }

    @Override
    public boolean match(String driver) {
        return provider.match(driver);
    }
}
