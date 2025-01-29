package se.fpcs.elpris.onoff.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceForHour {

    @NotNull
    @BsonProperty("price_source")
    @JsonProperty(value = "price_source", required = true)
    private PriceSource priceSource;

    @NotNull
    @BsonProperty("price_zone")
    @JsonProperty(value = "price_zone", required = true)
    private PriceZone priceZone;

    @NotNull
    @BsonProperty("sek_per_kwh")
    @JsonProperty(value = "sek_per_kwh", required = true)
    public Double sekPerKWh;

    @NotNull
    @BsonProperty("eur_per_kwh")
    @JsonProperty(value = "eur_per_kwh", required = true)
    public Double eurPerKWh;

    @NotNull
    @BsonProperty("exchange_rate")
    @JsonProperty(value = "exchange_rate", required = true)
    public Double exchangeRate;

    @NotNull
    @BsonProperty("price_time_ms")
    @JsonProperty(value = "price_time_ms", required = true)
    public Long priceTimeMs;

    @NotNull
    @BsonProperty("price_day")
    @JsonProperty(value = "price_day", required = true)
    public String priceDay;

    @NotNull
    @BsonProperty("price_hour")
    @JsonProperty(value = "price_hour", required = true)
    public String priceHour;

    @NotNull
    @BsonProperty("price_time_zone")
    @JsonProperty(value = "price_time_zone", required = true)
    public String priceTimeZone;
}
