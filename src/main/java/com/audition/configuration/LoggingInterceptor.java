package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    @Autowired
    private transient AuditionLogger logger;

    @Override
    public ClientHttpResponse intercept(
        final HttpRequest request,
        final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {
        final ClientHttpResponse response = execution.execute(request, body);
        logger.info(LOG, "REQUEST(" + request.getURI() + ") --> RESPONSE(" + response.getStatusCode() + ")", response);
        return response;
    }
}

