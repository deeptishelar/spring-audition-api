package com.audition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    @Autowired
    transient LoggingInterceptor loggingInterceptor;

    @Bean
    public ObjectMapper objectMapper() {
        // TODO configure Jackson Object mapper that
        //  1. allows for date format as yyyy-MM-dd
        //  2. Does not fail on unknown properties
        //  3. maps to camelCase
        //  4. Does not include null values or empty values
        //  5. does not write datas as timestamps.
        final JavaTimeModule module = new JavaTimeModule();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_PATTERN);
        final LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
        module.addSerializer(dateTimeSerializer);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(module);
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));
        // TODO use object mapper
        // TODO create a logging interceptor that logs request/response for rest template calls.

        restTemplate.setMessageConverters(
            Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper())));
        List<ClientHttpRequestInterceptor> interceptors
            = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(loggingInterceptor);
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }
}
