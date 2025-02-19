package se.fpcs.elpris.onoff.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "prices")
@CompoundIndex(
    name = "price_idx_1",
    def = "{'price_source': 1, 'priceZone': 1, 'priceDay': 1, 'priceHour': 1}",
    unique = true)
public class PriceForHour {

  @NotNull
  @JsonProperty(value = "price_source", required = true)
  private PriceSource priceSource;

  @NotNull
  @JsonProperty(value = "price_zone", required = true)
  private PriceZone priceZone;

  @NotNull
  @JsonProperty(value = "sek_per_kwh", required = true)
  public Double sekPerKWh;

  @NotNull
  @JsonProperty(value = "eur_per_kwh", required = true)
  public Double eurPerKWh;

  @NotNull
  @JsonProperty(value = "exchange_rate", required = true)
  public Double exchangeRate;

  @NotNull
  @JsonProperty(value = "price_time_ms", required = true)
  public Long priceTimeMs;

  @NotNull
  @JsonProperty(value = "price_day", required = true)
  public String priceDay;

  @NotNull
  @JsonProperty(value = "price_hour", required = true)
  public String priceHour;

  @NotNull
  @JsonProperty(value = "price_time_zone", required = true)
  public String priceTimeZone;

}
