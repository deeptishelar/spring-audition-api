package com.audition;

import static org.mockito.Mockito.doReturn;

import com.audition.common.logging.AuditionLogger;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AuditionIntegrationTests {

    private final String restURL = "https://jsonplaceholder.typicode.com/";
    @InjectMocks
    AuditionIntegrationClient client = new AuditionIntegrationClient();
    AuditionPost[] postsArray = new AuditionPost[3];
    AuditionPostComment[] commentsArray = new AuditionPostComment[3];
    AuditionPost auditionPost;
    @Mock
    private AuditionLogger logger;
    @Mock
    private RestTemplate restTemplate;
    @Value("${thirdparty.posts}")
    private String postsURL;
    @Value("${thirdparty.postsbyid}")
    private String postsByIdURL;
    @Value("${thirdparty.postwithcomments}")
    private String postWithCommentsURL;
    @Value("${thirdparty.commentsbypostid}")
    private String commentsByPostIdURL;
    @Value("${thirdparty.postsbyuserid}")
    private String postsByUserId;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(client, "postsURL", postsURL);
        ReflectionTestUtils.setField(client, "postsByIdURL", postsByIdURL);
        ReflectionTestUtils.setField(client, "postWithCommentsURL", postWithCommentsURL);
        ReflectionTestUtils.setField(client, "commentsByPostIdURL", commentsByPostIdURL);
        ReflectionTestUtils.setField(client, "postsByUserId", postsByUserId);

        AuditionPostComment auditionPostComment = new AuditionPostComment(1, 1, "test comment1", "test email1",
            "test body1");
        commentsArray[0] = auditionPostComment;
        auditionPostComment = new AuditionPostComment(1, 2, "test comment2", "test email2",
            "test body2");
        commentsArray[1] = auditionPostComment;
        auditionPostComment = new AuditionPostComment(2, 3, "test comment3", "test email3",
            "test body3");
        commentsArray[2] = auditionPostComment;
        auditionPostComment = new AuditionPostComment();
        auditionPostComment.setBody("test body 4");
        auditionPostComment.setId(5);
        auditionPostComment.setPostId(1);
        auditionPostComment.setName("test name");
        auditionPostComment.setEmail("test email");
        auditionPost = new AuditionPost();
        auditionPost = new AuditionPost(1, 1, "test title1", "test body1", null);
        postsArray[0] = auditionPost;
        auditionPost = new AuditionPost(1, 2, "test title2", "test body2", null);
        postsArray[1] = auditionPost;
        auditionPost = new AuditionPost(2, 1, "test title3", "test body3", null);
        postsArray[2] = auditionPost;
    }

    @Test
    void testGetPosts() {
        Mockito
            .when(restTemplate.getForEntity(postsURL, AuditionPost[].class))
            .thenReturn(new ResponseEntity<>(postsArray, HttpStatus.OK));
        final List<AuditionPost> posts = client.getPosts();
        Assertions.assertEquals(posts.size(), postsArray.length);

    }

    @Test
    void testGetPostsException() {
        String url = restURL + "posts1";
        Mockito
            .when(restTemplate.getForEntity(postsURL, AuditionPost[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Assertions.assertThrows(HttpClientErrorException.class,
            () -> client.getPosts());
    }

    @Test
    void testGetPostsById() {
        int postId = 2;
        AuditionPost auditionPostMock = Arrays.stream(postsArray).filter(p -> p.getId() == postId)
            .toList().get(0);
        final String format = MessageFormat.format(postsByIdURL, postId);
        Mockito
            .when(restTemplate.getForEntity(format, AuditionPost.class))
            .thenReturn(new ResponseEntity<>(auditionPostMock, HttpStatus.OK));
        final AuditionPost post = client.getPostById(postId);
        Assertions.assertEquals(post.getId(), postId);
    }

    @Test
    void testGetPostsByIdNeg() {
        int postId = 222;
        final String format = MessageFormat.format(postsByIdURL, postId);
        Mockito
            .when(restTemplate.getForEntity(format, AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Assertions.assertThrows(HttpClientErrorException.class,
            () -> client.getPostById(postId));
    }

    @Test
    void testGetPostsByIdNeg1() {
        int postId = 222;
        final String format = MessageFormat.format(postsByIdURL, postId);
        Mockito
            .when(restTemplate.getForEntity(format, AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class,
            () -> client.getPostById(postId));
    }

    @Test
    void testGetPostsByUserId() {
        int userId = 1;
        final String format = MessageFormat.format(postsByUserId, userId);
        Mockito
            .when(restTemplate.getForEntity(format, AuditionPost[].class))
            .thenReturn(new ResponseEntity<>(postsArray, HttpStatus.OK));
        final List<AuditionPost> posts = client.getPostsByUserId(userId);
        Assertions.assertEquals(posts.size(),
            Arrays.stream(postsArray).filter(p -> p.getUserId() == userId).toList().size());
    }

    @Test
    void testGetPostsByUserIdNegative() {
        int userId = 1;
        final String format = MessageFormat.format(postsByUserId, userId);
        Mockito
            .when(restTemplate.getForEntity(format, AuditionPost[].class))
            .thenReturn(new ResponseEntity<>(postsArray, HttpStatus.OK));
        final List<AuditionPost> posts = client.getPostsByUserId(userId);
        Assertions.assertEquals(posts.size(),
            Arrays.stream(postsArray).filter(p -> p.getUserId() == userId).toList().size());
    }

    @Test
    void testGetPostsByUserId2() {
        int userId = 2;
        final String format = MessageFormat.format(postsByUserId, userId);
        Mockito
            .when(restTemplate.getForEntity(format, AuditionPost[].class))
            .thenReturn(new ResponseEntity<>(postsArray, HttpStatus.OK));
        final List<AuditionPost> posts = client.getPostsByUserId(userId);
        Assertions.assertEquals(posts.size(),
            Arrays.stream(postsArray).filter(p -> p.getUserId() == userId).toList().size());
    }

    @Test
    void testGetPostWithComments() {
        int postId = 2;
        AuditionPost auditionPostMock = Arrays.stream(postsArray).filter(p -> p.getId() == postId)
            .toList().get(0);
        String url = restURL + "posts/" + postId;
        Mockito
            .when(restTemplate.getForEntity(url, AuditionPost.class))
            .thenReturn(new ResponseEntity<>(auditionPostMock, HttpStatus.OK));

        AuditionPostComment[] mockComments = Arrays.stream(commentsArray)
            .filter(c -> c.getPostId() == postId)
            .toArray(AuditionPostComment[]::new);
        url = restURL + "posts/" + postId + "/comments";
        doReturn(new ResponseEntity<>(mockComments, HttpStatus.OK)).when(
            restTemplate).getForEntity(url, AuditionPostComment[].class);
        final AuditionPost post = client.getPostWithComments(postId);
        Assertions.assertEquals(post.getId(), postId);

        Assertions.assertEquals(post.getId(), postId);
        Assertions.assertEquals(post.getComments().size(), mockComments.length);

    }

    @Test
    void testGetPostWithEmptyComments() {
        int postId = 2;
        AuditionPost auditionPostMock = Arrays.stream(postsArray).filter(p -> p.getId() == postId)
            .toList().get(0);
        String url = restURL + "posts/" + postId;
        Mockito
            .when(restTemplate.getForEntity(url, AuditionPost.class))
            .thenReturn(new ResponseEntity<>(auditionPostMock, HttpStatus.OK));

        AuditionPostComment[] mockComments = new AuditionPostComment[]{};
        url = restURL + "posts/" + postId + "/comments";
        doReturn(new ResponseEntity<>(mockComments, HttpStatus.OK)).when(
            restTemplate).getForEntity(url, AuditionPostComment[].class);
        final AuditionPost post = client.getPostWithComments(postId);
        Assertions.assertEquals(post.getId(), postId);

        Assertions.assertEquals(post.getId(), postId);
        Assertions.assertEquals(post.getComments().size(), 0);

    }

    @Test
    void testGetCommentsByPostId() {
        int postId = 2;
        String url = restURL + "comments?postId=" + postId;
        AuditionPostComment[] mockComments = Arrays.stream(commentsArray)
            .filter(c -> c.getPostId() == postId)
            .toArray(AuditionPostComment[]::new);
        doReturn(new ResponseEntity<>(mockComments, HttpStatus.OK)).when(
            restTemplate).getForEntity(url, AuditionPostComment[].class);
        final List<AuditionPostComment> comments = client.getCommentsByPostId(postId);
        for (AuditionPostComment comment : comments
        ) {
            Assertions.assertEquals(comment.getPostId(), postId);
        }

    }
}

