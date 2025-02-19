package se.fpcs.elpris.onoff.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.fpcs.elpris.onoff.security.Role;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

  @Id
  private String id;

  private String firstname;
  private String lastname;
  private String email;
  private String password;

  @Builder.Default
  private boolean accountNonExpired = true;
  @Builder.Default
  private boolean accountNonLocked = true;
  @Builder.Default
  private boolean credentialsNonExpired = true;
  @Builder.Default
  private boolean enabled = true;

  private Set<Role> roles;

  /**
   * ✅ Dynamically generates authorities from roles.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.name())) // Convert Enum to GrantedAuthority
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return email; // Spring Security requires a username field
  }
}
