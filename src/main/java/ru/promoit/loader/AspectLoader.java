package ru.promoit.loader;

import ru.promoit.aspect.Aspect;

public interface AspectLoader<T extends Aspect> {
    T load() throws Throwable;
}
