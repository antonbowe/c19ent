package de.antonbowe.c19ent.user;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserModel, String> {

  Optional<UserModel> findByUsername(String username);
}
