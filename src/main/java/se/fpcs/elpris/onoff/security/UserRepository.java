package se.fpcs.elpris.onoff.security;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import se.fpcs.elpris.onoff.user.User;

@RepositoryRestResource(exported = false)
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByEmail(String email);

}
