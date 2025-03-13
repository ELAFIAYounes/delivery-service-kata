package com.ordermanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI orderManagementOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Order Management Service API")
                .description("""
                    Spring Boot REST API for managing orders and refund requests.
                    Features:
                    - View customer order history
                    - Submit refund requests with evidence
                    """)
                .version("1.0.0")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("order-management")
                .packagesToScan("com.ordermanagement.rest.api")
                .build();
    }
}
