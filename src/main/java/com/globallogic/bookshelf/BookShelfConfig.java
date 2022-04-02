package com.globallogic.bookshelf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.Collections;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class BookShelfConfig implements WebMvcConfigurer {

    @Autowired(required = false)
    private ServletContext servletContext;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(createApiInfo());
    }

    /**
     * Method that creates short description on top of the Swagger api.
     * @return ApiInfo object with selected options
     */
    private ApiInfo createApiInfo() {
        return new ApiInfo("Bookshelf API",
                "This is a server API for internal Bookshelf",
                "1.00",
                "http://www.google.com",
                new Contact("Przemek", "http://www.google.com", "p.kondaszewski@globallogic.com"),
                "Open source license",
                "https://www.apache.org/licenses/LICENSE-2.0.html",
                Collections.emptyList());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry
                .addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/docs/");
    }

}