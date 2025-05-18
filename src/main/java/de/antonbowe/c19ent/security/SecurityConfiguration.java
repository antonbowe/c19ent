package de.antonbowe.c19ent.security;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfiguration {

  private final AuthTokenFilter authTokenFilter;
  private final List<String> allowedOrigins = List.of("http://localhost:4200");

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.addFilterAfter(this.authTokenFilter, UsernamePasswordAuthenticationFilter.class);

    http.authorizeHttpRequests(
        authorizeRequests ->
            authorizeRequests
                .requestMatchers("/public/**", "/error")
                .permitAll()
                .anyRequest()
                .authenticated()
        // permitting all requests to /v1/authentication/** without
        ); // any other request must be authenticated
    http.sessionManagement(
        session ->
            session.sessionCreationPolicy(
                SessionCreationPolicy
                    .STATELESS)); // JWT only works with stateless authentication (no sessions)
    // http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); //
    // probably not required
    http.csrf(AbstractHttpConfigurer::disable); // disables requirement of CSRF for POST requests
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    http.httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(this.allowedOrigins);
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(
        Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
