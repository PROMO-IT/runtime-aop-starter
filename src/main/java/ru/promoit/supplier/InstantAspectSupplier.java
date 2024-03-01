package ru.promoit.supplier;

import ru.promoit.aspect.Aspect;

import java.util.function.Supplier;

public class InstantAspectSupplier implements Supplier<Aspect> {
    private final Supplier<Aspect> supplier;
    private Aspect aspect;

    public InstantAspectSupplier(Supplier<Aspect> supplier) {
        this.supplier = supplier;
        aspect = supplier.get();
    }

    @Override
    public Aspect get() {
        return aspect;
    }
}
