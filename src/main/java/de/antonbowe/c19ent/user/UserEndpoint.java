package de.antonbowe.c19ent.user;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserEndpoint {

  private final UserService userService;

  @PostMapping
  @PreAuthorize("hasAuthority('USERS:CREATE')")
  public ResponseEntity<User> register(
      @Validated(Validation.Registration.class) @RequestBody User user, @AuthenticationPrincipal User principal) {
    log.info("Registering user: {}", user);
    return ResponseEntity.created(URI.create("/v1/user/" + user.getId()))
        .body(this.userService.registerNewUser(user, principal));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('USERS:VIEW:*', 'USERS:VIEW' + #id, 'USERS:MANAGE:' + #id, 'USERS:MANAGE:*') or #id == authentication.principal.id")
  public ResponseEntity<User> getUsers(@PathVariable("id") String id) {
    return ResponseEntity.ok(this.userService.loadUserById(id));
  }

  public interface Validation {
    interface Registration {}
  }
}
