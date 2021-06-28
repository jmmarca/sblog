package br.com.jmmarca.web.rest;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jmmarca.core.errors.BadRequestAlertException;
import br.com.jmmarca.model.Post;
import br.com.jmmarca.model.User;
import br.com.jmmarca.repository.PostRepository;
import br.com.jmmarca.services.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TestRestAPIs {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserService userService;

	private final Logger log = LoggerFactory.getLogger(PostResource.class);

	private static final String ENTITY_NAME = "post";

	@GetMapping("/api/test/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public String userAccess() {

		// apenas o criador pode remover
		final Optional<User> userAuth = userService.getUserWithAuthorities();
		if (!userAuth.isPresent()) {
			log.error("User is not logged in");
			throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
		}
		Optional<Post> post = postRepository.findById(Long.valueOf("5"));

		return ">>> User Contents!";
	}

	@GetMapping("/api/test/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return ">>> Admin Contents";
	}
}