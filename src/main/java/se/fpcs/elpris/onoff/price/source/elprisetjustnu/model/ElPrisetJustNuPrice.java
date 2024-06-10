
package se.fpcs.elpris.onoff.price.source.elprisetjustnu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Date;

@Value
@Builder
public class ElPrisetJustNuPrice {

    @JsonProperty("SEK_per_kWh")
    public BigDecimal sekPerKWh;
    @JsonProperty("EUR_per_kWh")
    public BigDecimal eurPerKWh;
    @JsonProperty("EXR")
    public BigDecimal exr;
    @JsonProperty("time_start")
    public Date timeStart;
    @JsonProperty("time_end")
    public Date timeEnd;

}
