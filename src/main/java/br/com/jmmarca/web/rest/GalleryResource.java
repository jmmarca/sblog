package br.com.jmmarca.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmmarca.core.errors.BadRequestAlertException;
import br.com.jmmarca.core.utils.HeaderUtil;
import br.com.jmmarca.core.utils.PaginationUtil;
import br.com.jmmarca.model.Gallery;
import br.com.jmmarca.model.Photo;
import br.com.jmmarca.model.User;
import br.com.jmmarca.repository.GalleryRepository;
import br.com.jmmarca.repository.PhotoRepository;
import br.com.jmmarca.services.UserService;

/**
 * @author jean.marca
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class GalleryResource {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MultipartResolver multipartResolver;

    private final Logger log = LoggerFactory.getLogger(GalleryResource.class);

    private static final String ENTITY_NAME = "gallery";

    @Value("${sblog.app.name}")
    private String applicationName;

    public GalleryResource(GalleryRepository galleryRepository, PhotoRepository photoRepository,
            UserService userService) {
        this.galleryRepository = galleryRepository;
        this.photoRepository = photoRepository;
        this.userService = userService;
    }

    @GetMapping("/gallerys")
    public ResponseEntity<List<Gallery>> findAll(Pageable pageable, UriComponentsBuilder uriBuilder) {
        Page<Gallery> page = galleryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(null), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/gallery")
    public ResponseEntity<Gallery> createGallery(@Valid @RequestBody Gallery gallery) throws URISyntaxException {
        log.debug("REST request to save gallery : {}", gallery);
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        // define o usuário que está criando
        gallery.setUser(userAuth.get());
        Gallery result = galleryRepository.save(gallery);
        return ResponseEntity
                .created(new URI("/api/gallery/" + result.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @PutMapping("/gallery")
    public ResponseEntity<Gallery> updateGallery(@Valid @RequestBody Gallery gallery) throws URISyntaxException {
        log.debug("REST request to update  " + ENTITY_NAME + "  : {}", gallery);
        if (gallery.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        // apenas o criador pode remover
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Optional<Gallery> galleryCommentDB = galleryRepository.findById(gallery.getId());
        if (galleryCommentDB.isPresent()) {
            if (userAuth.get().getId().equals(galleryCommentDB.get().getUser().getId())) {
                Gallery galleryToSave = galleryCommentDB.get();
                galleryToSave.setTitle(gallery.getTitle());
                gallery = galleryRepository.save(galleryToSave);
            } else {
                throw new BadRequestAlertException("Apenas o criador pode Modificar!", ENTITY_NAME, "param");
            }
        } else {
            throw new BadRequestAlertException("Registro não encontrado", ENTITY_NAME, "param");
        }
        return ResponseEntity.ok().headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gallery.getId().toString()))
                .body(gallery);
    }

    @GetMapping("/gallery/{id}")
    public ResponseEntity<Gallery> getGallery(@PathVariable Long id) {
        log.debug("REST request to get " + ENTITY_NAME + " : {}", id);
        Optional<Gallery> gallery = galleryRepository.findById(id);

        if (gallery.get().getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body(gallery.get());
    }

    @DeleteMapping("/gallery/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.debug("REST request to delete " + ENTITY_NAME + " : {}", id);

        // apenas o criador pode remover
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Optional<Gallery> gallery = galleryRepository.findById(id);
        if (gallery.isPresent()) {
            if (userAuth.get().getId().equals(gallery.get().getUser().getId())) {
                galleryRepository.deleteById(id);
            } else {
                throw new BadRequestAlertException("Apenas o criador da Galeria pode remover!", ENTITY_NAME, "param");
            }
        } else {
            throw new BadRequestAlertException("Galeria não encontrado! id=" + id, ENTITY_NAME, "param=" + id);
        }

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body("{\"message\":\"ok\"}");
    }

    /**
     * 
     * @param id
     * @return
     */
    @GetMapping("/galery/{id}/photos")
    public ResponseEntity<List<Photo>> findPhotoByGallery(@PathVariable Long id) {
        List<Photo> photos = photoRepository.findByGalleryId(id);
        return ResponseEntity.ok().body(photos);
    }

}
