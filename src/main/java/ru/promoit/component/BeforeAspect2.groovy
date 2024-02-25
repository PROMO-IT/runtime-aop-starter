package ru.promoit.component

import ru.promoit.aspect.BeforeAspect

class BeforeAspect2 implements BeforeAspect<TestComponent2> {
    Object[] beforeAdvice(TestComponent2 obj, Object[] args) throws Throwable {
        args[0] = args[0] + "bbb"
        return args
    }
}

