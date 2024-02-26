package ru.promoit.loader.provider;

import ru.promoit.loader.SourceTypeMatcher;

public interface GroovyAspectSourceProvider extends SourceTypeMatcher {
    String provide(String property) throws Throwable;
}
