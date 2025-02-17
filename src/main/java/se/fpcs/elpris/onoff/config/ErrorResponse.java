package se.fpcs.elpris.onoff.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Error response model containing error details")
public class ErrorResponse {

  @Schema(description = "Type of error", example = "Forbidden")
  private String error;

  @Schema(description = "Detailed error message", example = "Access Denied")
  private String message;

  @Schema(description = "HTTP status code", example = "403")
  private int status;

}
