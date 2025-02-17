package se.fpcs.elpris.onoff.price.source.elprisetjustnu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class EPJN_Price {

  @JsonProperty("SEK_per_kWh")
  public Double sekPerKWh;
  @JsonProperty("EUR_per_kWh")
  public Double eurPerKWh;
  @JsonProperty("EXR")
  public Double exr;
  @JsonProperty("time_start")
  public String timeStart;
  @JsonProperty("time_end")
  public String timeEnd;

}
