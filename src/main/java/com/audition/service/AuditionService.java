package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    @Autowired
    transient private AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    public AuditionPost getPostById(final int postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public List<AuditionPostComment> getCommentsByPostId(final int postId) {
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }

    public AuditionPost getPostWithComments(final int postid) {
        return auditionIntegrationClient.getPostWithComments(postid);
    }

    public List<AuditionPost> getPostsByUserId(final int userId) {
        return auditionIntegrationClient.getPostsByUserId(userId);
    }
}
