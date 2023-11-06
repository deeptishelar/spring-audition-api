package com.audition;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import com.audition.service.AuditionService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuditionServiceTests {

    List<AuditionPost> postsList = new ArrayList<>();
    List<AuditionPostComment> comments = new ArrayList<>();
    AuditionPost auditionPost;
    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;
    @InjectMocks
    private AuditionService auditionService = new AuditionService();

    @BeforeEach
    void init() {
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
    void testGetPosts() {
        Mockito
            .when(auditionService.getPosts())
            .thenReturn(postsList);
        final List<AuditionPost> posts = auditionIntegrationClient.getPosts();
        Assertions.assertEquals(posts.size(), postsList.size());
    }

    @Test
    void testGetPostById() {
        final int postId = 1;
        Mockito
            .when(auditionService.getPostById(postId))
            .thenReturn(auditionPost);
        AuditionPost post = auditionIntegrationClient.getPostById(postId);
        Assertions.assertEquals(post.getId(), postId);
    }

    @Test
    void testGetCommentsByPostId() {
        final int postId = 2;
        Mockito
            .when(auditionService.getCommentsByPostId(postId))
            .thenReturn(comments);
        final List<AuditionPostComment> comments1 = auditionIntegrationClient.getCommentsByPostId(postId);
        Assertions.assertEquals(comments1.size(), this.comments.size());
    }

    @Test
    void testGetPostWithComments() {
        final int postId = 2;
        AuditionPost post = new AuditionPost(2, 2, "test title3", "test body3", comments);
        Mockito
            .when(auditionService.getPostWithComments(postId))
            .thenReturn(post);
        final AuditionPost postWithComments = auditionIntegrationClient.getPostWithComments(postId);
        Assertions.assertEquals(postWithComments.getComments().size(), this.comments.size());
    }

    @Test
    void testGetPostsByUserId() {
        final int userId = 2;
        final List<AuditionPost> filteredList = postsList.stream().filter(p -> p.getUserId() == userId).toList();
        Mockito
            .when(auditionService.getPostsByUserId(userId))
            .thenReturn(filteredList);
        List<AuditionPost> posts = auditionIntegrationClient.getPostsByUserId(userId);
        for (AuditionPost post : posts
        ) {
            Assertions.assertEquals(post.getUserId(), userId);
        }
    }

}
