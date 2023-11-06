package com.audition;//package com.audition;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.audition.common.logging.AuditionLogger;
import com.audition.web.AuditionController;
import com.audition.web.advice.ExceptionControllerAdvice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest

public class MyGlobalExceptionHandlerTest {

    @Mock
    AuditionController controller;
    @InjectMocks
    ExceptionControllerAdvice controllerAdvice = new ExceptionControllerAdvice();
    private MockMvc mockMvc;
    @Mock
    private AuditionLogger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(controllerAdvice)
            .build();
    }

    @Test
    void testGlobalExceptionHandlerError() throws Exception {
        when(controller.getPosts(1))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
            .andExpect(result -> Assertions.assertEquals("application/problem+json",
                result.getResponse().getContentType()))
            .andExpect(jsonPath("$.status").value(404))
            .andReturn();
    }

    @Test
    void test1() throws Exception {
        when(controller.getPosts(1))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
            .andExpect(result -> Assertions.assertEquals("application/problem+json",
                result.getResponse().getContentType()))
            .andExpect(jsonPath("$.status").value(400))
            .andReturn();
    }

    @Test
    void test2() throws Exception {
        when(controller.getPosts(1))
            .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
            .andExpect(result -> Assertions.assertEquals("application/problem+json",
                result.getResponse().getContentType()))
            .andExpect(jsonPath("$.status").value(403))
            .andReturn();
    }

    @Test
    void test3() throws Exception {
        when(controller.getPosts(1))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
            .andExpect(result -> Assertions.assertEquals("application/problem+json",
                result.getResponse().getContentType()))
            .andExpect(jsonPath("$.status").value(500))
            .andReturn();
    }

    @Test
    void test4() throws Exception {
        when(controller.getPosts(1))
            .thenThrow(new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED));
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
            .andExpect(result -> Assertions.assertEquals("application/problem+json",
                result.getResponse().getContentType()))
            .andExpect(jsonPath("$.status").value(405))
            .andReturn();
    }

//    @Test
//    void test5() throws Exception {
//        when(controller.getPosts(1))
//            .thenThrow(new ConstraintViolationException(...));
//        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
//            .andExpect(result -> Assertions.assertEquals("application/problem+json",
//                result.getResponse().getContentType()))
//            .andExpect(jsonPath("$.status").value(405))
//            .andReturn();
//    }
}
