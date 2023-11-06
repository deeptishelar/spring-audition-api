package com.audition.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new ResponseHeaderInjector());
    }
}
