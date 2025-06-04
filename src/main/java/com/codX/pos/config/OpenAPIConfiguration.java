package com.codX.pos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI defineOpenApi() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Development Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://your-production-domain.com");
        prodServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setName("POS System Development Team");
        contact.setEmail("admin@possystem.com");
        contact.setUrl("https://your-company-website.com");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info information = new Info()
                .title("POS System API")
                .version("1.0.0")
                .description("Comprehensive API documentation for POS System with role-based access control. " +
                        "This system supports Super Admin, Company Admin, Branch Admin, POS User, Employee, and Customer roles.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(information)
                .servers(List.of(devServer, prodServer))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter JWT token (without 'Bearer ' prefix)");
    }
}
