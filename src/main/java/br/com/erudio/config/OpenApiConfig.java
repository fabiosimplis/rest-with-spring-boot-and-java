package br.com.erudio.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java 21 and Spring Boot 3 - Foo Bar")
                        .version("v1")
                        .description("API for study REST")
                        .termsOfService("https://github.com/fabiosimplis")
                        .license(
                                new License()
                                        .name("Apache 2.0")
                                        .url("https://github.com/fabiosimplis/rest-with-spring-boot-and-java")));
    }
}
