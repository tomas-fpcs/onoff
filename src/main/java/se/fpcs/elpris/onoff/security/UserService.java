package se.fpcs.elpris.onoff.security;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByApiKey(String apiKey);
}
