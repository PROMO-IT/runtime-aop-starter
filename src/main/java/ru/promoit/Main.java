package ru.promoit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.promoit.component.TestComponent1;
import ru.promoit.component.TestComponent2;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        TestComponent1 component1 = context.getBean(TestComponent1.class);
        TestComponent2 component2 = context.getBean(TestComponent2.class);

        component1.testMethod1("123");
        component2.testMethod3("456");
    }
}