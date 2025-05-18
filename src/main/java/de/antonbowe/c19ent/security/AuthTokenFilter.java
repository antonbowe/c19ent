package de.antonbowe.c19ent.security;

import de.antonbowe.c19ent.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthTokenFilter extends OncePerRequestFilter {

  private final AuthTokenService authTokenService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!this.authTokenService.hasAuthTokenInHeader(request)) {
      log.debug("No auth token found in request");
      filterChain.doFilter(request, response);
      return;
    }

    String authToken = this.authTokenService.extractAuthTokenFromHeader(request);
    log.debug("Found auth token: {}", authToken);

    User user = this.authTokenService.validateAuthToken(authToken);
    log.debug("Found user: {}", user);

    PreAuthenticatedAuthenticationToken authentication =
        new PreAuthenticatedAuthenticationToken(user, authToken, user.getAuthorities());

    log.debug("Authenticated user: {}", user.getUsername());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
