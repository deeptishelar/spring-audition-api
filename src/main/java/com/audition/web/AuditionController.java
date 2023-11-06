package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuditionController {

    @Autowired
    transient private AuditionService auditionService;

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(final @RequestParam("userId") Optional<Integer> userId) {
        // TODO Add logic that filters response data based on the query param
        if (userId.isPresent()) {
            return auditionService.getPostsByUserId(userId.get());
        }
        return auditionService.getPosts();
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPosts(
        @PathVariable @Positive final int id) {
        // DONE TODO Add input validation
        return auditionService.getPostById(id);
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/
    @RequestMapping(value = "/comments/{postId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPostComment> getCommentsByPostId(@PathVariable("postId") final int postId) {
        return auditionService.getCommentsByPostId(postId);
    }

    @RequestMapping(value = "/posts/{postId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostWithComments(@PathVariable("postId") final int postId) {
        return auditionService.getPostWithComments(postId);
    }
}
