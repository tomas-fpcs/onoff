package se.fpcs.elpris.onoff.price;

import java.util.EnumMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PriceUpdaterStatus {

  private Map<PriceSource, Boolean> statusMap = new EnumMap<>(PriceSource.class);

  public boolean isReady(PriceSource priceSource) {
    Boolean ready = this.statusMap.get(priceSource);
    return ready != null && ready;
  }

  public void setReady(PriceSource priceSource) {
    this.statusMap.put(priceSource, true);
  }

}
