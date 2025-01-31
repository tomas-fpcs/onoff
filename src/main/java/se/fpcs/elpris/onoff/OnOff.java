package se.fpcs.elpris.onoff;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OnOff {

  @JsonProperty("on")
  private boolean on;

  @JsonProperty("max-price")
  private int maxPrice;

  @JsonProperty("price-spot")
  private int priceSpot;

  @JsonProperty("price-supplier")
  private int priceSupplier;

  @JsonProperty("user-name")
  private String userName;

  @JsonProperty("server_time")
  private String serverTime;

}
