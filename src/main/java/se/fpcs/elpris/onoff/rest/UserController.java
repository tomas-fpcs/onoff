package se.fpcs.elpris.onoff.rest;

import static se.fpcs.elpris.onoff.Constants.HAS_ROLE_ADMIN;
import static se.fpcs.elpris.onoff.Constants.ONOFF_V1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.fpcs.elpris.onoff.security.GenericApiResponses;
import se.fpcs.elpris.onoff.security.UserRepository;
import se.fpcs.elpris.onoff.user.User;


@RestController
@GenericApiResponses
@RequiredArgsConstructor
@Log4j2
public class UserController {

  private final UserRepository userRepository;

  @Operation(summary = "Create a User")
  @ApiResponse(responseCode = "200",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = User.class))})
  @PreAuthorize(HAS_ROLE_ADMIN)
  @PostMapping(value = ONOFF_V1 + "/user")
  public ResponseEntity<?> createUser(@RequestBody User user) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userRepository.save(user));

  }

  @Operation(summary = "Get all Users")
  @GetMapping(value = ONOFF_V1 + "/user")
  @PreAuthorize(HAS_ROLE_ADMIN)
  @SuppressWarnings("java:S1452")
  public ResponseEntity<?> findAll() {

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(userRepository.findAll());

  }

}


