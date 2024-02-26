package ru.promoit.loader;

import ru.promoit.aspect.Aspect;

public interface AspectSourceLoader<T extends Aspect> extends AspectLoader<T>, SourceTypeMatcher {
}
