package ru.promoit.component;

import org.springframework.stereotype.Component;

@Component
public class TestComponent2 {
    public String testMethod3(String s) {
        return s + "test3";
    }

    public String testMethod4(String s) {
        return s + "test4";
    }
}
