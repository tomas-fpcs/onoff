package se.fpcs.elpris.onoff.db;

public class DatabaseOperationException extends RuntimeException {

  public DatabaseOperationException(String message) {
    super(message);
  }

  public DatabaseOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}

