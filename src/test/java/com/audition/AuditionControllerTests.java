package com.audition;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.LoggingInterceptor;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import com.audition.service.AuditionService;
import com.audition.web.AuditionController;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

//@RunWith(SpringRunner.class)
@WebMvcTest(AuditionController.class)
@Import(AuditionController.class)

public class AuditionControllerTests {

    @MockBean
    transient AuditionService service;
    @Autowired
    transient WebApplicationContext wac;
    @MockBean
    transient LoggingInterceptor loggingInterceptor;
    transient List<AuditionPost> postsList = new ArrayList<>();
    transient List<AuditionPostComment> comments = new ArrayList<>();
    transient AuditionPost auditionPost;
    @Autowired
    transient private MockMvc mockMvc;
    //    @Mock
//    private RestTemplate restTemplate;
//    @Mock
//    private ExceptionControllerAdvice advice;
    @MockBean
    private AuditionLogger logger;

    @BeforeEach
    public void beforeTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        AuditionPostComment auditionPostComment = new AuditionPostComment(1, 1, "test comment1", "test email1",
            "test body1");
        comments.add(auditionPostComment);
        auditionPostComment = new AuditionPostComment(1, 2, "test comment2", "test email2",
            "test body2");
        comments.add(auditionPostComment);
        auditionPostComment = new AuditionPostComment(2, 3, "test comment3", "test email3",
            "test body3");
        comments.add(auditionPostComment);

        auditionPost = new AuditionPost(1, 1, "test title1", "test body1", null);
        postsList.add(auditionPost);
        auditionPost = new AuditionPost(1, 2, "test title2", "test body2", null);
        postsList.add(auditionPost);
        auditionPost = new AuditionPost(2, 1, "test title3", "test body3", null);
        postsList.add(auditionPost);
    }


    @Test
    void test1() throws Exception {
        when(service.getPosts()).thenReturn(postsList);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts"))
            .andExpect(status().isOk());
    }

    @Test
    void test11() throws Exception {
        when(service.getCommentsByPostId(1)).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/comments/1"))
            .andExpect(status().isOk());
    }

    @Test
    void test12() throws Exception {
        when(service.getPostsByUserId(1)).thenReturn(postsList);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts?userId=1"))
            .andExpect(status().isOk());
    }

    @Test
    void test13() throws Exception {
        when(service.getPostById(1)).thenReturn(auditionPost);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1"))
            .andExpect(status().isOk());
    }

    @Test
    void test14() throws Exception {
        when(service.getPostWithComments(1)).thenReturn(auditionPost);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/1/comments"))
            .andExpect(status().isOk());
    }


    @Test
    void test2() throws Exception {
        when(service.getPosts()).thenThrow(HttpClientErrorException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts1"))
            .andExpect(status().isNotFound());
    }


    @Test
    void test() throws Exception {
        when(service.getPostsByUserId(1)).thenThrow(HttpClientErrorException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts1"))
            .andExpect(status().isNotFound());
    }


    @Test
    void testBadRequest() throws Exception {
        when(service.getPosts()).thenThrow(HttpClientErrorException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("https://jsonplaceholder.typicode.com/posts/d"))
            .andExpect(status().isBadRequest())
            .andExpect(
                result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));
    }

}
