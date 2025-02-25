package com.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class DeliveryServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceApplication.class);

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DeliveryServiceApplication.class);
        
        // Set default profile if none specified
        if (System.getProperty("spring.profiles.active") == null) {
            app.setAdditionalProfiles("dev");
        }
        
        app.run(args);
    }

    @PostConstruct
    public void init() {
        String[] activeProfiles = env.getActiveProfiles();
        logger.info("Active profiles: {}", String.join(", ", activeProfiles));
        
        if (activeProfiles.length == 0) {
            logger.warn("No active profiles set. Using default profile.");
        }
        
        logger.info("Application started with the following configuration:");
        logger.info("Server port: {}", env.getProperty("server.port"));
        logger.info("Database URL: {}", env.getProperty("spring.datasource.url"));
        logger.info("JPA Show SQL: {}", env.getProperty("spring.jpa.show-sql"));
    }
}
