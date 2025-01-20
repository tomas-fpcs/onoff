package se.fpcs.elpris.onoff.config;

public class CouldNotCreateWebClientException extends RuntimeException {
    public CouldNotCreateWebClientException() {
        super("Could not create webclient");
    }
}
