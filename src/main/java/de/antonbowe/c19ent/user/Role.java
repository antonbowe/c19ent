package de.antonbowe.c19ent.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Role {
  USER(100, new String[] {"USERS:CREATE"}),
  MANAGER(1000, new String[] {}),
  ADMINISTRATOR(10000, new String[] {});

  private int power;
  private String[] permissions;

  public boolean isHigherOrEqual(Role role) {
    return this.power >= role.power;
  }

  public boolean isHigher(Role role) {
    return this.power > role.power;
  }

  public Collection<String> getAuthorities() {
    List<String> grantedAuthorities = new ArrayList<>(List.of("ROLE_" + this.name()));
    for (Role allAssignedRole : this.getAllAssignedRoles()) {
      grantedAuthorities.addAll(allAssignedRole.getAuthorities());
    }
    grantedAuthorities.addAll(Arrays.asList(this.permissions));
    return grantedAuthorities;
  }

  public Collection<Role> getAllAssignedRoles() {
    return Arrays.stream(values()).filter(role -> role != this && !role.isHigher(this)).collect(Collectors.toSet());
  }
}
