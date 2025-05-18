package de.antonbowe.c19ent.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.antonbowe.c19ent.user.User;
import de.antonbowe.c19ent.user.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Reader;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthTokenService {

  private final ObjectMapper objectMapper;
  private final UserService userService;

  @Value("${authToken.secret}")
  private String secret;

  @Value("${authToken.expiration}")
  private Long expiration;

  private Key key;

  public boolean hasAuthTokenInHeader(HttpServletRequest request) {
    return request.getHeader("Authorization") != null
        && request.getHeader("Authorization").startsWith("Bearer ");
  }

  public String extractAuthTokenFromHeader(HttpServletRequest request) {
    if (!hasAuthTokenInHeader(request)) {
      return null;
    }
    return request.getHeader("Authorization").substring(7);
  }

  public String generateAuthToken(User user) {
      log.info("Generating auth token for user {}", user.getUsername());
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    final Map<String, Object> claims = Map.of("user", user);
    return Jwts.builder()
        .claims(claims)
        .subject(user.getId())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(this.key)
        .compact();
  }

  public User validateAuthToken(String authToken) {
    final Claims claims;
    try {
      claims =
          Jwts.parser()
                  .json(new Deserializer<Map<String,?>>() {
                    @Override
                    public Map<String, ?> deserialize(byte[] bytes) throws DeserializationException {
                        try {
                            Map<String, Object> claimsMap = objectMapper.readValue(bytes, new TypeReference<>() {});

                            if (claimsMap.containsKey("user")) {
                                Map<String, Object> userMap = (Map<String, Object>) claimsMap.get("user");
                                User user = objectMapper.convertValue(userMap, User.class);
                                claimsMap.put("user", user);
                            }

                            return claimsMap;

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public Map<String, ?> deserialize(Reader reader) throws DeserializationException {
                      try {
                        Map<String, Object> claimsMap = objectMapper.readValue(reader, new TypeReference<>() {});

                        if (claimsMap.containsKey("user")) {
                          Map<String, Object> userMap = (Map<String, Object>) claimsMap.get("user");
                          User user = objectMapper.convertValue(userMap, User.class);
                          claimsMap.put("user", user);
                        }

                        return claimsMap;

                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    }
                  })
              .verifyWith((SecretKey) this.key)
              .build()
              .parseSignedClaims(authToken)
              .getPayload();
    } catch (MalformedJwtException exception) {
      log.debug(
          "Could not validate authentication token: Token malformed ({})", exception.getMessage());
      throw new BadCredentialsException("Invalid token", exception);
    } catch (ExpiredJwtException exception) {
      log.debug(
          "Could not validate authentication token: Token expired ({})", exception.getMessage());
      throw new BadCredentialsException("Token expired", exception);
    } catch (UnsupportedJwtException exception) {
      log.debug(
          "Could not validate authentication token: Token version is not supported ({})",
          exception.getMessage());
      throw new BadCredentialsException("Invalid token", exception);
    }

    return claims.get("user", User.class).markTokenized();
  }

  @PostConstruct
  private void generateKey() {
    this.key = Jwts.SIG.HS256.key().build();
    // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
  }
}
