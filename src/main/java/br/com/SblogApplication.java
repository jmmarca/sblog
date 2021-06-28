package br.com;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.jmmarca.model.Role;
import br.com.jmmarca.model.User;
import br.com.jmmarca.model.Enums.RoleName;
import br.com.jmmarca.property.FileStorageProperties;
import br.com.jmmarca.repository.RoleRepository;
import br.com.jmmarca.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties({
	FileStorageProperties.class
})
public class SblogApplication {

	@Autowired
    PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(SblogApplication.class, args);
	}

	/**
	 * Inicializa o sistema com os dados básicos de acesso
	 * 
	 * @param userRepository
	 * @return
	 */
	@Bean
	CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
		//Adiciona na primeira execução apenas
		if (roleRepository.count() == 0) {

			Role adminRole = null;
			//create role admin
			Role role = new Role();
			role.setName(RoleName.ROLE_ADMIN);
			adminRole = roleRepository.save(role);
			//Role user
			role = new Role();
			role.setName(RoleName.ROLE_USER);
			roleRepository.save(role);

			Set<Role> roles = new HashSet<>();
			roles.add(adminRole);
			User user = new User();
			user.setName("Administrador");
			user.setRoles(roles);
			user.setPassword(encoder.encode("admin"));
			user.setUsername("admin");
			user.setEmail("jmmarca@gmail.com");
			userRepository.save(user);
		}
		return null;
	}

}
