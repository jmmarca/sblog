package br.com.jmmarca.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jmmarca.core.errors.BadRequestAlertException;
import br.com.jmmarca.core.utils.HeaderUtil;
import br.com.jmmarca.model.Post;
import br.com.jmmarca.model.PostComment;
import br.com.jmmarca.model.User;
import br.com.jmmarca.repository.PostCommentRepository;
import br.com.jmmarca.repository.PostRepository;
import br.com.jmmarca.services.UserService;

/**
 * Crud of PostComments
 * 
 * @author jean.marca
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PostCommentResource {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory.getLogger(PostCommentResource.class);

    private static final String ENTITY_NAME = "post-comment";

    @Value("${sblog.app.name}")
    private String applicationName;

    public PostCommentResource(PostRepository postRepository, PostCommentRepository postCommentRepository,
            UserService userService) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.userService = userService;
    }

    /**
     * 
     * Create Comment in Post
     * 
     * @param postComment
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/post-comments")
    public ResponseEntity<PostComment> createPostComment(@Valid @RequestBody PostComment postComment) throws URISyntaxException {
        log.debug("REST request to save post Comment : {}", postComment);
        if (postComment.getId() != null) {
            throw new BadRequestAlertException("O comentário já possui id cadastrado", ENTITY_NAME, null);
        }
        Long id = postComment.getPost().getId();
        Optional<Post> post = postRepository.findById(postComment.getPost().getId());
        if(!post.isPresent())
        {
            throw new BadRequestAlertException("Post "+id+" não encontrado!", ENTITY_NAME, null);
        }       
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        //define o usuário que está criando o post
        postComment.setUser(userAuth.get());         

        postComment = postCommentRepository.save(postComment);
        return ResponseEntity
                .created(new URI("/api/post-comment/" + postComment.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, postComment.getId().toString()))
                .body(postComment);
    }

    /**
     * Update Comment in Post
     * 
     * @param postComment
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/post-comments")
    public ResponseEntity<PostComment> updatePostComment(@Valid @RequestBody PostComment postComment) throws URISyntaxException {
        log.debug("REST request to update  " + ENTITY_NAME + "  : {}", postComment);
        if (postComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        // apenas o criador pode remover
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Optional<PostComment> postCommentDB = postCommentRepository.findById(postComment.getId());
        if (postCommentDB.isPresent()) {
            if (userAuth.get().getId().equals(postCommentDB.get().getUser().getId())) {
                PostComment postCommentToSave = postCommentDB.get();
                postCommentToSave.setContent(postComment.getContent());
                postComment = postCommentRepository.save(postCommentToSave);
            } else {
                throw new BadRequestAlertException("Apenas o criador do Comentário pode Modificar!", ENTITY_NAME, "param");
            }
        } else {
            throw new BadRequestAlertException("Comentário não encontrado", ENTITY_NAME, "param");
        }      
        return ResponseEntity.ok()
                .headers(
                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postComment.getId().toString()))
                .body(postComment);
    }

    /**
     * Get Comment in Post By Id
     * 
     * @param id
     * @return
     */
    @GetMapping("/post-comments/{id}")
    public ResponseEntity<PostComment> getPostComment(@PathVariable Long id) {
        log.debug("REST request to get " + ENTITY_NAME + " : {}", id);
        Optional<PostComment> postComment = postCommentRepository.findById(id);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body(postComment.get());
    }

    /**
     * Remove comment in Post
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/post-comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete comment" + ENTITY_NAME + " : {}", id);

        // apenas o criador pode remover
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Optional<PostComment> postComment = postCommentRepository.findById(id);
        if (postComment != null) {
            if (userAuth.get().getId().equals(postComment.get().getUser().getId())) {
                postCommentRepository.deleteById(id);
            } else {
                throw new BadRequestAlertException("Apenas o criador do Comentário pode remover!", ENTITY_NAME, "param");
            }
        }

        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

}
