package br.com.jmmarca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.jmmarca.model.Post;
import br.com.jmmarca.model.Enums.StatusPost;

/**
 * @author jean.marca
 */
@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Post findByStatus(StatusPost status);

    Page<Post> findAllByStatus(StatusPost status, Pageable pageRequest);

    Page<Post> findAll(Pageable pageRequest);

}
