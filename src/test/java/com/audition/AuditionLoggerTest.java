package com.audition;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
public class AuditionLoggerTest {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionLoggerTest.class);
    AuditionLogger aLogger;

    @BeforeEach
    public void init() {
        aLogger = new AuditionLogger();
    }

    @Test
    void test1() {
        aLogger.info(LOG, "test");
        Assertions.assertThatNoException();

    }

    @Test
    void test2() {
        aLogger.info(LOG, "test", "test detail");
        Assertions.assertThatNoException();
    }

    @Test
    void test3() {
        aLogger.warn(LOG, "warn");
        Assertions.assertThatNoException();
    }

    @Test
    void test4() {
        aLogger.debug(LOG, "debug");
        Assertions.assertThatNoException();
    }

    @Test
    void test5() {
        aLogger.error(LOG, "Error");
        Assertions.assertThatNoException();
    }

    @Test
    void test6() {
        aLogger.logErrorWithException(LOG, "Error", new SystemException("test"));
        Assertions.assertThatNoException();
    }

    @Test
    void test7() {
        aLogger.logHttpStatusCodeError(LOG, "Error", HttpStatus.NOT_FOUND.value());
        Assertions.assertThatNoException();
    }

    @Test
    void test8() {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        aLogger.logStandardProblemDetail(LOG, problemDetail,
            new SystemException("test", new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED)));
        Assertions.assertThatNoException();
    }
}
