package se.fpcs.elpris.onoff.security;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HardcodedUserService implements UserService {
    @Override
    public Optional<User> getUserByApiKey(String apiKey) {

        if (apiKey == null) {
            return Optional.empty();
        }

        if (apiKey.equals("47e4de80-9881-4914-9b78-8b3706be4235")) {
            return Optional.of(
                    User.builder()
                            .name("Tomas")
                            .build());
        }

        return Optional.empty();
    }
}
