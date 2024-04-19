package br.com.erudio.config;

import br.com.erudio.serializationconverter.YamlJackson2HttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final MediaType MEDIA_TYPE_APPLICATION_YAML = MediaType.valueOf("application/x-yaml");

    @Value("${cors.originPatterns:default}")
    private String corsOriginPatterns = "";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrigins = corsOriginPatterns.split(",");
        registry.addMapping("/**")//Todas as rotas da nossa API
                //.allowedMethods("GET", "POST", "PUT") //permite por verbos
                .allowedMethods("*")
                .allowedOrigins(allowedOrigins)
                .allowCredentials(true);// pertmite credenciais
    }

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
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YAML);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.add(new YamlJackson2HttpMessageConverter());
    }
}
