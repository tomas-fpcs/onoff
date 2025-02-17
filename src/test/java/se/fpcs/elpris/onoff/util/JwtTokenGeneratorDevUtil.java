package se.fpcs.elpris.onoff.util;

import java.util.Map;
import java.util.Set;
import se.fpcs.elpris.onoff.security.JwtService;
import se.fpcs.elpris.onoff.security.Role;
import se.fpcs.elpris.onoff.user.User;

/**
 * Used for development only
 */
public class JwtTokenGeneratorDevUtil {

  public static void main(String[] args) {

    var userToken = generateToken(
        "Sven",
        "Usersson",
        "user@example.com",
        "password",
        Role.ROLE_USER);

    var adminToken = generateToken(
        "Karl",
        "Adminsson",
        "admin@example.com",
        "password",
        Role.ROLE_ADMIN);

    System.out.println("userToken:\n" + userToken);
    System.out.println("adminToken:\n" + adminToken);

  }

  public static String generateToken(
      final String firstname,
      final String lastname,
      final String username,
      final String password,
      final Role role) {

    JwtService jwtService = new JwtService();

    User user = User.builder()
        .firstname(firstname)
        .lastname(lastname)
        .email(username)
        .password(password)
        .build();

    Map<String, Object> extraClaims = Map.of("roles", Set.of(role));
    return jwtService.generateToken(extraClaims, user);
  }
}
