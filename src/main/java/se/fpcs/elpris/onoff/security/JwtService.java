package se.fpcs.elpris.onoff.security;

import static java.util.Objects.requireNonNull;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class JwtService {

  private static final String JWT_SECRET_KEY = System.getenv("JWT_SECRET_KEY");

  public JwtService() {
    requireNonNull(JWT_SECRET_KEY,
        "Environment variable " + JWT_SECRET_KEY + " not set");
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public Optional<String> extractUsername(final String token) {
    try {
      return Optional.of(extractClaim(token, Claims::getSubject));
    } catch (Exception e) {
      log.trace("extractUsername threw {}: {}", e.getClass().getSimpleName(), e.getMessage());
      return Optional.empty();
    }
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000)))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

  }

  public boolean isTokenValid(final String token, final UserDetails userDetails) {

    //TODO extractUsername Optional, is this good code?
    final var isValidUsername =
        userDetails.getUsername().equals(extractUsername(token).get()) &&
            !isTokenExpired(token);

    if (!isValidUsername && log.isTraceEnabled()) {
      log.trace("JWT token is not valid, username mismatch");
    }

    return isValidUsername;
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private <T> T extractClaim(final String token, Function<Claims, T> claimsResolver) {
    return claimsResolver.apply(extractAllClaims(token));
  }

  private Claims extractAllClaims(final String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
  }

  private static SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY));
  }

}
