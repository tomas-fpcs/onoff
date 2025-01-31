package se.fpcs.elpris.onoff.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @NotNull
  @BsonProperty("first_name")
  @JsonProperty(value = "first_name", required = true)
  private String firstName;

  @NotNull
  @BsonProperty("last_name")
  @JsonProperty(value = "last_name", required = true)
  private String lastName;

  @NotNull
  @BsonProperty("email")
  @JsonProperty(value = "email", required = true)
  private String email;

  @NotNull
  @BsonProperty("api_key")
  @JsonProperty(value = "api_key", required = true)
  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format"
  )
  private String apiKey;

}
