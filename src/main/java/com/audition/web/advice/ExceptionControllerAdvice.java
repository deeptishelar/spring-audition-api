package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@Validated
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    private static final String ERROR_MESSAGE = " Error Code from Exception could not be mapped to a valid HttpStatus Code - ";
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";
    @Autowired
    transient private AuditionLogger logger;

    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {
        final HttpStatusCode statusCode = e.getStatusCode();
        String message;
        String title;
        if (statusCode == HttpStatus.NOT_FOUND) {
            message = "Cannot find the item with given id ";
            title = "Resource Not Found";
        } else if (statusCode == HttpStatus.BAD_REQUEST) {
            message = "The requested URL doesn't have a valid structure or contains invalid characters ";
            title = "Bad Request";
        } else if (statusCode == HttpStatus.FORBIDDEN) {
            message = "The client does not have access rights to the content";
            title = "Forbidden";
        } else if (statusCode == INTERNAL_SERVER_ERROR) {
            message = "The server has encountered a situation it is unable to handle";
            title = "Internal Server Error";
        } else {
            message = "ERROR_MESSAGE";
            title = DEFAULT_TITLE;
        }
        final SystemException systemException = new SystemException(message, title, statusCode.value());
        return handleSystemException(systemException);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(final ConstraintViolationException e) {
        return handleMainException(e);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleMainException(final Exception e) {
        // TODO Add handling for Exception
        logger.logErrorWithException(LOG, e.getMessage(), e);
        return createProblemDetail(e, getHttpStatusCodeFromException(e));
    }

    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException e) {
        // TODO Add Handling for SystemException
        logger.logHttpStatusCodeError(LOG, e.getMessage(), e.getStatusCode());
        return createProblemDetail(e, getHttpStatusCodeFromSystemException(e));
    }


    private ProblemDetail createProblemDetail(final Exception exception,
        final HttpStatusCode statusCode) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setDetail(getMessageFromException(exception));
        if (exception instanceof SystemException) {
            problemDetail.setTitle(((SystemException) exception).getTitle());
        } else {
            problemDetail.setTitle(DEFAULT_TITLE);
        }
        logger.logStandardProblemDetail(LOG, problemDetail, exception);
        return problemDetail;
    }

    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_MESSAGE;
    }

    private HttpStatusCode getHttpStatusCodeFromSystemException(final SystemException exception) {
        try {
            return HttpStatusCode.valueOf(exception.getStatusCode());
        } catch (final IllegalArgumentException iae) {
            logger.info(LOG, ERROR_MESSAGE + exception.getStatusCode());
            return INTERNAL_SERVER_ERROR;
        }
    }

    private HttpStatusCode getHttpStatusCodeFromException(final Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            return ((HttpClientErrorException) exception).getStatusCode();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        }
        return INTERNAL_SERVER_ERROR;
    }
}



