package ru.promoit.component

import ru.promoit.aspect.BeforeAspect

class BeforeAspect1 implements BeforeAspect {
    Object[] beforeAdvice(Object obj, Object[] args) throws Throwable {
        args[0] = args[0] + "aaa"
        return args
    }
}
