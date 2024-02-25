package ru.promoit.component;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TestComponent1 {
    public String testMethod1(String s) {
        return s + "test1";
    }

    public String testMethod2(String s) {
        return s + "test2";
    }
}
