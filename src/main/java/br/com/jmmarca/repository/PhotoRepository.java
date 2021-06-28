package br.com.jmmarca.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.jmmarca.model.Photo;

/**
 * @author jean.marca
 */
@Repository
public interface PhotoRepository extends CrudRepository<Photo, Long> {

    public List<Photo> findByGalleryId(Long id);

    public List<Photo> findByPostId(Long id);
}
