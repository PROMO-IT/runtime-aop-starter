package ru.promoit.loader;

import groovy.lang.GroovyClassLoader;
import ru.promoit.aspect.Aspect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class GroovyAspectClassLoader<T extends Aspect> implements AspectSourceLoader<T> {
    private final String clazz;

    public GroovyAspectClassLoader(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public T load() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> aClass;
        try (GroovyClassLoader loader = new GroovyClassLoader()) {
            aClass = loader.loadClass(clazz);
        }

        return (T) aClass.getDeclaredConstructor(null).newInstance();
    }

    @Override
    public boolean match(String driver) {
        return "class".equals(driver);
    }
}
