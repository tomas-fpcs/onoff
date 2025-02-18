package se.fpcs.elpris.onoff.security.auth;

import static se.fpcs.elpris.onoff.Constants.ONOFF_AUTH;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping(value = ONOFF_AUTH + "/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest registerRequest
  ) {
    return ResponseEntity.ok(
        authenticationService.register(registerRequest));
  }

  @PostMapping(value = ONOFF_AUTH + "/authenticate")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody AuthenticationRequest authenticationRequest
  ) {
    return ResponseEntity.ok(
        authenticationService.authenticate(authenticationRequest));
  }

}
