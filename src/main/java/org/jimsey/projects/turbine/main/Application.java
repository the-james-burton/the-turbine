package org.jimsey.projects.turbine.main;

import org.jimsey.projects.turbine.services.Ping;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("spring-application.xml");
 
        Ping ping = (Ping) context.getBean("ping");
        long message = ping.ping();
        System.out.println(message);
        
        context.close();
 
    }
}
