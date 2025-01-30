package se.fpcs.elpris.onoff.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.fpcs.elpris.onoff.user.User;
import se.fpcs.elpris.onoff.user.UserService;

import static se.fpcs.elpris.onoff.Constants.ONOFF_V1;


@RestController
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create a User")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))})
    @PostMapping(value = ONOFF_V1 + "/user")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdUser);
    }

    @Operation(summary = "Get all Users")
    @GetMapping(value = ONOFF_V1 +"/user")
    @SuppressWarnings("java:S1452")
    public ResponseEntity<?> findAll() {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll());

    }

}


