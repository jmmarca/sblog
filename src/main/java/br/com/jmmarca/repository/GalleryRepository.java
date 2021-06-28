package br.com.jmmarca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.jmmarca.model.Gallery;
import br.com.jmmarca.model.Post;

/**
 * @author jean.marca
 */
@Repository
public interface GalleryRepository extends CrudRepository<Gallery, Long> {
    
    Page<Gallery> findAll(Pageable pageRequest);

}
