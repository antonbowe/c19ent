package de.antonbowe.c19ent.user;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService implements UserDetailsService {

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private final UserRepository userRepository;
  private final UserFactory userFactory;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.userRepository
        .findByUsername(username)
        .map(
            userModel -> {
              User user = this.userFactory.toObject(userModel);
              user.getAuthorityStrings().addAll(this.getAllAuthorities(userModel));
              return user;
            })
        .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
  }

  private List<String> getAllAuthorities(UserModel userModel) {
    List<String> authorities = new ArrayList<>();
    authorities.addAll(userModel.getRole().getAuthorities());
    authorities.addAll(
        userModel.getAllTeamIds().stream().map(teamId -> "USERS:MANAGE:" + teamId).toList());
    return authorities;
  }

  public User registerNewUser(User userDetailsToCreate, User issuer) {
    log.info("Registering new user {}", userDetailsToCreate.getUsername());

    UserModel userModel = this.userFactory.toModel(userDetailsToCreate);
    userModel.setPassword(this.passwordEncoder.encode(userDetailsToCreate.getPassword()));

    UserModel createdUserModel = this.userRepository.save(userModel);
    User createdUser = this.userFactory.toObject(createdUserModel);

    if (issuer != null) {
      this.subordinateUser(issuer, createdUser);
      createdUser = this.userFactory.toObject(createdUserModel);
    }

    return createdUser;
  }

  private void subordinateUser(User manager, User subordinate) {
    UserModel managerModel =
        this.userRepository
            .findById(manager.getId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Subordinate user failed: Manager ID not found in database"));
    UserModel subordinateModel =
        this.userRepository
            .findById(subordinate.getId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Subordinate user failed: Subordinate ID not found in database"));

    managerModel.getTeam().add(subordinateModel);
    subordinateModel.setManager(managerModel);

    this.userRepository.saveAll(List.of(subordinateModel, managerModel));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return this.passwordEncoder;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(this);
    provider.setPasswordEncoder(this.passwordEncoder);
    return provider;
  }

  public User loadUserById(String id) {
    UserModel userModel =
        this.userRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));
    return this.userFactory.toObject(userModel);
  }

  public Page<User> getUserPageByPrincipal(Pageable pageable) {
    return this.userRepository
        .findAll(pageable)
        .map(userModel -> this.userFactory.toObject(userModel).setPassword(null));
  }

  public User updateUser(String id, User user) {
    UserModel userModel = this.userRepository.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));

    if (user.getUsername() != null && !user.getUsername().equals(userModel.getUsername())) {
      userModel.setUsername(user.getUsername());
    }

    if (user.getEmail() != null && !user.getEmail().equals(userModel.getEmail())) {
      userModel.setEmail(user.getEmail());
    }

    if (user.getFirstName() != null && !user.getFirstName().equals(userModel.getFirstName())) {
      userModel.setFirstName(user.getFirstName());
    }

    if (user.getLastName() != null && !user.getLastName().equals(userModel.getLastName())) {
      userModel.setLastName(user.getLastName());
    }

    return this.userFactory.toObject(this.userRepository.save(userModel));
  }
}
