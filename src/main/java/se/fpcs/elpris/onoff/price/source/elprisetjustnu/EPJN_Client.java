package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.model.EPJN_Price;

public interface EPJN_Client {

  @GetExchange("/api/v1/prices/{year}/{month}-{day}_{priceZone}.json")
  EPJN_Price[] getPrices(
      @PathVariable("year") String year,
      @PathVariable("month") String month,
      @PathVariable("day") String day,
      @PathVariable("priceZone") String priceZone
  );

}
