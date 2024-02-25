package ru.promoit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.promoit.component.TestComponent1;
import ru.promoit.component.TestComponent2;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
    }
}