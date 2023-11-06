package com.audition.configuration;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ResponseHeaderInjector implements HandlerInterceptor {

    // TODO Inject openTelemetry trace and span Ids in the response headers.
    private transient final Tracer tracer = GlobalOpenTelemetry.getTracer("app");

    /**
     * Executed before actual handler is executed
     **/
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        final Span span = tracer.spanBuilder("audition").startSpan();
        span.setAttribute("custom.info", "some info");

        try (Scope scope = span.makeCurrent()) {
            response.addHeader("spanId", span.getSpanContext().getSpanId());
            response.addHeader("traceId", span.getSpanContext().getTraceId());
        } finally {
            span.end();
        }
        return true;
    }

    /**
     * Executed before after handler is executed
     **/
    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
        final ModelAndView modelAndView) throws Exception {
        //SET span id in response headers
    }
}
