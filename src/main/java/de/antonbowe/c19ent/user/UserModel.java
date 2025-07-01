package de.antonbowe.c19ent.user;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserModel {

  @Id
  @Getter @Setter @GeneratedValue private Long id;

  @Getter @Setter private String username;
  @Getter @Setter private String password;

  @Getter @Setter private String email;
  @Getter @Setter private String firstName;
  @Getter @Setter private String lastName;

  @Getter @Setter private Role role;

  @Getter @OneToMany(mappedBy = "manager")
  private Set<UserModel> team = new HashSet<>();

  @ManyToOne
  @Getter @Setter UserModel manager;

  @Getter @Setter private boolean enabled;

}