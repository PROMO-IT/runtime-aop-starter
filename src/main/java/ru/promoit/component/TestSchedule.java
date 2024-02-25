package ru.promoit.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestSchedule {
    @Autowired
    private TestComponent1 component1;

    @Autowired
    private TestComponent2 component2;

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void test() {
        System.out.println(component1.testMethod1("123"));
        System.out.println(component2.testMethod3("456"));
    }
}
