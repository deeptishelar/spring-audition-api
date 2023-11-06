package com.audition.integration;

import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.LoggingInterceptor;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    @Value("${thirdparty.posts}")
    private transient String postsURL;
    @Value("${thirdparty.postsbyid}")
    private transient String postsByIdURL;
    @Value("${thirdparty.postwithcomments}")
    private transient String postWithCommentsURL;
    @Autowired
    private transient RestTemplate restTemplate;
    @Value("${thirdparty.postsbyuserid}")
    private transient String postsByUserId;
    @Value("${thirdparty.commentsbypostid}")
    private transient String commentsByPostIdURL;
    @Autowired
    private transient AuditionLogger logger;

    public List<AuditionPost> getPosts() {
        // DONE TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        final AuditionPost[] posts = restTemplate.getForEntity(postsURL, AuditionPost[].class).getBody();
        logger.info(LOG, "All posts", posts);
        return Arrays.stream(posts).toList();
    }

    public AuditionPost getPostById(final int id) {
        // done : TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        final String restUrl = MessageFormat.format(postsByIdURL, id);
        final AuditionPost post = restTemplate.getForEntity(restUrl, AuditionPost.class).getBody();
        logger.info(LOG, "post by id : " + id, post);
        return post;
    }


    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.
    public AuditionPost getPostWithComments(final int postId) {
        String restUrl = MessageFormat.format(postsByIdURL, postId);
        final AuditionPost post = restTemplate.getForEntity(restUrl, AuditionPost.class).getBody();
        restUrl = MessageFormat.format(postWithCommentsURL, postId);
        //todo streams API parallel key fork join
        final AuditionPostComment[] comments = restTemplate.getForEntity(restUrl, AuditionPostComment[].class)
            .getBody();
        if (ArrayUtils.isEmpty(comments)) {
            post.setComments(new ArrayList<>());
        }
        post.setComments(Arrays.stream(comments).toList());
        logger.info(LOG, "comments for the post of id : " + postId, post);
        return post;

    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    public List<AuditionPostComment> getCommentsByPostId(final int postId) {
        final String restUrl = MessageFormat.format(commentsByPostIdURL, postId);
        final AuditionPostComment[] comments = restTemplate.getForEntity(restUrl, AuditionPostComment[].class)
            .getBody();
        logger.info(LOG, "comments for the post of id : " + postId, comments);
        return Arrays.stream(comments).toList();
    }

    public List<AuditionPost> getPostsByUserId(final int userId) {
        final String restUrl = MessageFormat.format(postsByUserId, userId);
        final AuditionPost[] posts = restTemplate.getForEntity(restUrl, AuditionPost[].class).getBody();
        final List<AuditionPost> postsByUser = Arrays.stream(posts).toList().stream()
            .filter(p -> p.getUserId() == userId)
            .toList();
        logger.info(LOG, "posts by user Id  : " + userId, postsByUser);
        return postsByUser;
    }

}
