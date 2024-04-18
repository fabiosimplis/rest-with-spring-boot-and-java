package br.com.erudio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Via Query Param. http://localhost:8080/api/person/v1?mediaType=xml

        /*configurer.favorParameter(true)//Aceita parametros
                .parameterName("mediaType")//nome do parametro
                .ignoreAcceptHeader(true)//
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);*/

        // Via HEADER PARAM. http://localhost:8080/api/person/v1
        //Content Negotiation via Header Parameter
        configurer.favorParameter(false)//Aceita parametros
                .ignoreAcceptHeader(false)//
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
