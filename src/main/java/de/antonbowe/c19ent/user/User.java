package de.antonbowe.c19ent.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"accountNonLocked", "accountNonExpired", "credentialsNonExpired", "authorities"})
public class User implements UserDetails {

  @JsonIgnore private boolean tokenized = false;

  @Null(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private String id;

  @NotNull(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private String username;

  @NotNull(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private String password;

  @NotNull(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private String email;

  @NotNull(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private String firstName;

  @NotNull(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private String lastName;

  @NotNull(groups = {UserEndpoint.Validation.Registration.class})
  @Getter
  @Setter
  private Role role;

  @JsonIgnore private final Set<String> teamIds = new HashSet<>();

  @JsonIgnore private String managerId;

  @Getter
  @Setter
  @AssertTrue(groups = {UserEndpoint.Validation.Registration.class})
  private Boolean enabled;

  @Getter
  private final Collection<String> authorityStrings = new ArrayList<>();

  public User markTokenized() {
    this.tokenized = true;
    return this;
  }

  public Set<String> getTeamIds() {
    if (this.tokenized)
      throw new IllegalStateException("Teams data not accessible (user is tokenized)");
    return this.teamIds;
  }

  public String getManagerId() {
    if (this.tokenized)
      throw new IllegalStateException("Managers data not accessible (user is tokenized)");
    return managerId;
  }

  public void setManagerId(String managerId) {
    if (this.tokenized)
      throw new IllegalStateException("Managers data not accessible (user is tokenized)");
    this.managerId = managerId;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorityStrings.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
  }

}
