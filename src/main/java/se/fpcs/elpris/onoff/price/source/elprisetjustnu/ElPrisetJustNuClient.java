package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.model.ElPrisetJustNuPrice;

public interface ElPrisetJustNuClient {

    @GetExchange("/api/v1/prices/{year}/{month}-{day}_{priceZone}.json")
    ElPrisetJustNuPrice[] getPrices(
            @PathVariable("year") String year,
            @PathVariable("month") String month,
            @PathVariable("day") String day,
            @PathVariable("priceZone") String priceZone
    );

}
