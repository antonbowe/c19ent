package de.antonbowe.c19ent.user;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

  public UserModel toModel(User user) {
    return this.toModel(null, user);
  }

  public UserModel toModel(UserModel userModel, User user) {
    if (userModel == null) userModel = new UserModel();

    if (user.getUsername() != null) userModel.setUsername(user.getUsername());
    if (user.getPassword() != null) userModel.setPassword(user.getPassword());
    if (user.getEmail() != null) userModel.setEmail(user.getEmail());
    if (user.getFirstName() != null) userModel.setFirstName(user.getFirstName());
    if (user.getLastName() != null) userModel.setLastName(user.getLastName());
    if (user.getRole() != null) userModel.setRole(user.getRole());

    if (user.getTeamIds() != null) {
      userModel.getTeam().clear();
      userModel
          .getTeam()
          .addAll(
              user.getTeamIds().stream().map(id -> new UserModel().setId(id)).collect(Collectors.toSet()));
    }

    userModel.setEnabled(user.getEnabled() == null ? userModel.isEnabled() : user.getEnabled());

    return userModel;
  }

  public User toObject(UserModel userModel) {
    User user = new User();

    user.setId(userModel.getId());
    user.setUsername(userModel.getUsername());
    user.setPassword(userModel.getPassword());
    user.setEmail(userModel.getEmail());
    user.setFirstName(userModel.getFirstName());
    user.setLastName(userModel.getLastName());
    user.setRole(userModel.getRole());
    user.getTeamIds().clear();
    user.getTeamIds()
        .addAll(userModel.getTeam().stream().map(UserModel::getId).collect(Collectors.toSet()));
    user.setManagerId(userModel.getManager() != null ? userModel.getManager().getId() : null);

    user.setEnabled(userModel.isEnabled());

    return user;
  }
}
