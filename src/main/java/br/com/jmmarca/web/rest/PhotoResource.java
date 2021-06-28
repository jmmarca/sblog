package br.com.jmmarca.web.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.jmmarca.core.errors.BadRequestAlertException;
import br.com.jmmarca.core.utils.HeaderUtil;
import br.com.jmmarca.model.Gallery;
import br.com.jmmarca.model.Photo;
import br.com.jmmarca.model.User;
import br.com.jmmarca.model.Payload.UploadFileResponse;
import br.com.jmmarca.repository.GalleryRepository;
import br.com.jmmarca.repository.PhotoRepository;
import br.com.jmmarca.services.FileStorageService;
import br.com.jmmarca.services.UserService;

/**
 * @author jean.marca
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PhotoResource {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MultipartResolver multipartResolver;

    private final Logger log = LoggerFactory.getLogger(PhotoResource.class);

    private static final String ENTITY_NAME = "photo";

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${sblog.app.name}")
    private String applicationName;

    @Value("${file.upload-dir}")
    private String pathUpload;

    public PhotoRepository getPhotoRepository() {
        return photoRepository;
    }

    public void setPhotoRepository(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @DeleteMapping("/photo/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.debug("REST request to delete " + ENTITY_NAME + " : {}", id);

        // apenas o criador pode remover
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Optional<Photo> photo = photoRepository.findById(id);
        if (photo.isPresent()) {
            if (userAuth.get().getId().equals(photo.get().getUser().getId())) {

                String path = "gallery/" + photo.get().getGallery().getId() + "/" + photo.get().getFileName();
                try {
                    // Load file as Resource
                    Resource resource = fileStorageService.loadFileAsResource(path);
                    resource.getFile().delete();
                } catch (Exception e) {
                    log.error("Erro ao remover foto!", e);
                    //throw new BadRequestAlertException("Erro ao remover foto!", ENTITY_NAME, "param");
                }
                photoRepository.deleteById(id);
            } else {
                throw new BadRequestAlertException("Apenas o criador da foto pode remover!", ENTITY_NAME, "param");
            }
        } else {
            throw new BadRequestAlertException("Foto não encontrada! id=" + id, ENTITY_NAME, "param=" + id);
        }

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body("{\"message\":\"ok\"}");
    }

    @PostMapping(value = "/photo/gallery-upload/{id}")
    public UploadFileResponse uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file)
            throws IOException {

        Optional<Gallery> gallery = galleryRepository.findById(id);
        if (!gallery.isPresent()) {
            throw new BadRequestAlertException("Galeria não encontrada! id=" + id, ENTITY_NAME, "param=" + id);
        }
        final Optional<User> userAuth = userService.getUserWithAuthorities();
        if (!userAuth.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("Usuário deve estar logado", ENTITY_NAME, "param");
        }
        Photo photo = new Photo();
        photo.setGallery(gallery.get());
        String fileName = fileStorageService.storeFile(file, "gallery/" + id.toString());
        photo.setFileName(fileName);
        photo.setUser(userAuth.get());

        photo = photoRepository.save(photo);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/photo/" + photo.getId() + "/").toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    /**
     * Retorna a foto de acordo o ID
     * 
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/photo/download/{id}")
    public ResponseEntity<Resource> downloadPhoto(@PathVariable Long id, HttpServletRequest request) {

        Optional<Photo> photo = photoRepository.findById(id);

        if (!photo.isPresent()) {
            throw new BadRequestAlertException("Foto não encontrada! id=" + id, ENTITY_NAME, "param=" + id);
        }
        Photo photobd = photo.get();
        String path = "gallery/" + photobd.getGallery().getId() + "/" + photobd.getFileName();

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(path);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Tipo do arquivo não definido!");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
