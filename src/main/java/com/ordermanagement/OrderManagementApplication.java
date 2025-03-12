package com.ordermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class OrderManagementApplication {
    private static final Logger logger = LoggerFactory.getLogger(OrderManagementApplication.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void logApplicationStartup() {
        String protocol = "http";
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "/");
        String hostAddress = "localhost";
        
        logger.info("""
            
            Application is running!
            Local: \t\t{}://{}:{}{}
            Profile(s): \t{}
            """,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OrderManagementApplication.class);
        app.run(args);
    }
}
