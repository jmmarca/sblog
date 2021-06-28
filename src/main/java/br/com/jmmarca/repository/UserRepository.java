package br.com.jmmarca.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.jmmarca.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * 
     * @param username
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * 
     * @param username
     * @return
     */
    Optional<User> findByEmail(String username);

    Boolean existsByUsernameIgnoreCase(String username);

    Boolean existsByEmailIgnoreCase(String email);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByUsernameIgnoreCase(String login);

}