package br.com.jmmarca.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmmarca.core.errors.BadRequestAlertException;
import br.com.jmmarca.core.utils.HeaderUtil;
import br.com.jmmarca.core.utils.PaginationUtil;
import br.com.jmmarca.model.Post;
import br.com.jmmarca.model.PostComment;
import br.com.jmmarca.model.User;
import br.com.jmmarca.model.Enums.StatusPost;
import br.com.jmmarca.repository.PostCommentRepository;
import br.com.jmmarca.repository.PostRepository;
import br.com.jmmarca.services.UserService;

/**
 * @author jean.marca
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PostResource {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory.getLogger(PostResource.class);

    private static final String ENTITY_NAME = "post";

    @Value("${sblog.app.name}")
    private String applicationName;

    public PostResource(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * List posts Publicated
     */
    @GetMapping("/posts/find-publicated")
    public ResponseEntity<List<Post>> findPublicated(Pageable pageable, UriComponentsBuilder uriBuilder) {
        Page<Post> page = postRepository.findAllByStatus(StatusPost.PUBLISHED, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(null), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * List all posts (PENDING/PUBLISHED)
     * 
     * @param pageable
     * @param uriBuilder
     * @return
     */
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> findAll(Pageable pageable, UriComponentsBuilder uriBuilder) {
        Page<Post> page = postRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(null), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Create Post
     * 
     * @param post
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/post")
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to save post : {}", post);
        if (post.getId() != null) {
            throw new BadRequestAlertException("O post já possui id cadastrado", ENTITY_NAME, null);
        }
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        // define o usuário que está criando o post
        post.setUser(userAuth.get());
        Post result = postRepository.save(post);
        return ResponseEntity
                .created(new URI("/api/post/" + result.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * Update Post
     * 
     * @param post
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/post")
    public ResponseEntity<Post> updatePost(@Valid @RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to update  " + ENTITY_NAME + "  : {}", post);
        if (post.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Post result = postRepository.save(post);
        return ResponseEntity.ok()
                .headers(
                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, post.getId().toString()))
                .body(result);
    }

    /**
     * Get Post
     * 
     * @param id
     * @return
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        log.debug("REST request to get " + ENTITY_NAME + " : {}", id);
        Optional<Post> post = postRepository.findById(id);

        if (post.get().getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body(post.get());
    }

    /**
     * Remove Post
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.debug("REST request to delete " + ENTITY_NAME + " : {}", id);

        // apenas o criador pode remover
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            if (userAuth.get().getId().equals(post.get().getUser().getId())) {
                postRepository.deleteById(id);
            } else {
                throw new BadRequestAlertException("Apenas o criador do post pode remover!", ENTITY_NAME, "param");
            }
        } else {
            throw new BadRequestAlertException("Post não encontrado! id=" + id, ENTITY_NAME, "param=" + id);
        }

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body("{\"message\":\"ok\"}");
    }

    /**
     * Publish post
     * 
     * @param id
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/post/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to publicar Post  " + ENTITY_NAME + "  : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<Post> retPost = postRepository.findById(id);
        Post post = null;

        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        if (!retPost.isPresent()) {
            throw new BadRequestAlertException("Post não encontrado! id=" + id, ENTITY_NAME, "param=" + id);
        } else {
            post = retPost.get();
            if (!userAuth.get().getId().equals(post.getUser().getId())) {
                post.setStatus(StatusPost.PUBLISHED);
                post = postRepository.save(post);
            } else {
                throw new BadRequestAlertException("Apenas o criador do post pode modificar!", ENTITY_NAME, "param");
            }
        }

        return ResponseEntity.ok()
                .headers(
                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, post.getId().toString()))
                .body(post);
    }

    @GetMapping("/post/{id}/comments")
    public ResponseEntity<Collection<PostComment>> findAllComments(@PathVariable Long id) {

        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            throw new BadRequestAlertException("Post " + id + " não encontrado!", ENTITY_NAME, null);
        }
        List<PostComment> findByPostId = postCommentRepository.findByPostId(id);
        return ResponseEntity.ok().body(findByPostId);
    }

}
