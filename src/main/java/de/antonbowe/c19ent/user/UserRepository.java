package de.antonbowe.c19ent.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {

  Optional<UserModel> findByUsername(String username);
}
