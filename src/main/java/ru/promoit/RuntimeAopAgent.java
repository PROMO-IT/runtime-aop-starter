package ru.promoit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import ru.promoit.agent.*;
import ru.promoit.loader.AspectLoadManager;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class RuntimeAopAgent {
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Agent works!");

        List<AbstractInterceptor> interceptors = AspectLoadManager.initInterceptors(args);

        AgentBuilder agentBuilder = new AgentBuilder
                .Default(new ByteBuddy(ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V17)))
//                .with(AgentBuilder.Listener.StreamWriting.toSystemError().withErrorsOnly())
                .ignore(ElementMatchers.none());


        interceptors.forEach(interceptor ->
            agentBuilder.type(ElementMatchers.hasSuperType(ElementMatchers.named(interceptor.clazz))
                        .or(ElementMatchers.named(interceptor.clazz)))
                .transform(((builder, typeDescription, classLoader, javaModule) -> builder
                        .method(ElementMatchers.named(interceptor.methodName))
                        .intercept(MethodDelegation.withDefaultConfiguration().withBinders(Morph.Binder.install(Morpher.class))
                                .to(interceptor)))).installOn(instrumentation));

        System.out.println("Interceptor installed!!!");
    }
}
