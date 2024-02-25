package ru.promoit.component

import ru.promoit.aspect.BeforeAspect

class BeforeAspect1 implements BeforeAspect<TestComponent1> {
    Object[] beforeAdvice(TestComponent1 obj, Object[] args) throws Throwable {
        args[0] = args[0] + "aaa"
        return args
    }
}

