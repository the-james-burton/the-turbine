package org.jimsey.projects.turbine.main;

import java.util.Arrays;

import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.jimsey.projects.turbine.spring.components.Ping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="org.jimsey.projects.turbine.spring.components, org.jimsey.projects.turbine.spring.controller, org.jimsey.projects.turbine.camel.routes")
public class Application extends CamelConfiguration {

    public static void main(String[] args) {
        ConfigurableApplicationContext spring = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = spring.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

        Ping ping = (Ping) spring.getBean("ping");
        long message = ping.ping();
        System.out.println(message);
        
    }

}