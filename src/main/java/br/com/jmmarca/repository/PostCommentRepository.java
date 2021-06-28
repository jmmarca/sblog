package br.com.jmmarca.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.jmmarca.model.PostComment;

/**
 * @author jean.marca
 */
@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Long> {

    public List<PostComment> findByPostId(Long id);

}
