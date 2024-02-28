import ru.promoit.aspect.BeforeAspect

class BeforeAspect2 implements BeforeAspect {
    Object[] beforeAdvice(Object obj, Object[] args) throws Throwable {
        args[0] = args[0] + "bbb"
        return args
    }
}

