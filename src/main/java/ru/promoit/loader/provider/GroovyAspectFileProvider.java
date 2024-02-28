package ru.promoit.loader.provider;

import java.io.FileInputStream;

public class GroovyAspectFileProvider implements GroovyAspectSourceProvider {
    @Override
    public boolean match(String driver) {
        return "file".equals(driver);
    }

    @Override
    public String provide(String property) throws Throwable {
        return new String(new FileInputStream(property).readAllBytes());
    }
}
