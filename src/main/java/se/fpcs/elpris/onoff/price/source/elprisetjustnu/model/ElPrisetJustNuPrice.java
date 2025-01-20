
package se.fpcs.elpris.onoff.price.source.elprisetjustnu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class ElPrisetJustNuPrice {

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
