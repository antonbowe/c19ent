package de.antonbowe.c19ent.security;

import de.antonbowe.c19ent.user.Role;
import de.antonbowe.c19ent.user.User;
import de.antonbowe.c19ent.user.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class AuthenticationEndpoint {

  private final AuthTokenService authTokenService;
  private final UserService userService;

  @PostMapping("/login")
  public Map<String, Object> login(@AuthenticationPrincipal User user) {
    log.info("Login user: {}", user.getUsername());
    return Map.of("token", this.authTokenService.generateAuthToken(user), "user", user);
  }
}
