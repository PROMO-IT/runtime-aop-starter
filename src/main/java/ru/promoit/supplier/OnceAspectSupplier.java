package ru.promoit.supplier;

import ru.promoit.aspect.Aspect;

import java.util.Objects;
import java.util.function.Supplier;

public class OnceAspectSupplier implements Supplier<Aspect> {
    private final Supplier<Aspect> supplier;
    private Aspect aspect;

    public OnceAspectSupplier(Supplier<Aspect> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Aspect get() {
        if (Objects.isNull(aspect)) {
            aspect = supplier.get();
        }

        return aspect;
    }
}
