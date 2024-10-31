package com.techtest.scrumretroapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaRepositories
@EntityScan
public class ScrumRetroApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrumRetroApiApplication.class, args);
    }
}


/**
 * Five Stereotype annotations - show what the role of a class is and are automatically registered as Spring Beans
 * - @Component - this is a generic base annotation
 * - @Service - used to define the service layer (acts between controller and repository)
 * - @Repository
 * - @Controller
 * - @RestController
 *
 * Dependency injection is when an object receives other objects that it depends on instead of initialising them itself
 * - Main benefit is more flexible code, dependency decoupling, easier testing
 *
 * Inversion of Control is a design principle that decouples components by inverting the flow fo control. Instead of a
 * component managing its dependencies, this is delegated to an external entity (in Spring's case, it is the IoC container)
 */
