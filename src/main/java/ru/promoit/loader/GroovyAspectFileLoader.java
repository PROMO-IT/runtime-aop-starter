package ru.promoit.loader;

import groovy.lang.GroovyClassLoader;
import ru.promoit.aspect.Aspect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class GroovyAspectFileLoader<T extends Aspect> implements AspectSourceLoader<T> {
    private final String filename;

    public GroovyAspectFileLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public T load() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass;
        try (GroovyClassLoader loader = new GroovyClassLoader()) {
            aClass = loader.parseClass(new File(filename));
        }

        return (T) aClass.getDeclaredConstructor(null).newInstance();
    }

    @Override
    public boolean match(String driver) {
        return "file".equals(driver);
    }
}
