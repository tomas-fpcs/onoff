package se.fpcs.elpris.onoff.price;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(exported = false)
public interface PriceRepository extends MongoRepository<PriceForHour, String> {

  Optional<PriceForHour> findByPriceSourceAndPriceZoneAndPriceDayAndPriceHour(
      String priceSource,
      String priceZone,
      String priceDay,
      String priceHour);

}
