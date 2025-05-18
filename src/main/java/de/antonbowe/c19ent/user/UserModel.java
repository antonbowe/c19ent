package de.antonbowe.c19ent.user;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@NoArgsConstructor
@Accessors(chain = true)
public class UserModel {

  @Id @Getter @Setter private String id;

  @Getter @Setter private String username;
  @Getter @Setter private String password;

  @Getter @Setter private String email;
  @Getter @Setter private String firstName;
  @Getter @Setter private String lastName;

  @Getter @Setter private Role role;

  @Getter @DBRef private Set<UserModel> team = new HashSet<>();

  @Getter @Setter @DBRef UserModel manager;

  @Getter @Setter private boolean enabled;


  public List<String> getAllTeamIds() {
    System.out.println(this.team.size() + "team members");
    List<String> teamIds = new ArrayList<>();
    for (UserModel user : team) {
      System.out.println(user.getId() +" managed by " + this.username);
      teamIds.add(user.getId());
      teamIds.addAll(user.getAllTeamIds());
    }
    return teamIds;
  }

}
