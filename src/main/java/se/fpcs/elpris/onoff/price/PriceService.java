package se.fpcs.elpris.onoff.price;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.fpcs.elpris.onoff.MongoDbService;

@Service
@Profile("!test")
@Log4j2
public class PriceService extends MongoDbService {

  //private static final String PRICES_COLLECTION_NAME = "prices";

  //@SuppressWarnings("java:S1640")
  //Map<PriceSource, MongoCollection<Document>> pricesCollections = new HashMap<>();

  //@PostConstruct
//  @Override
//  protected void initMongoDB() {
//    super.initMongoDB();
//
//    try {
//      Arrays.stream(PriceSource.values())
//          .forEach(priceSource -> {
//            MongoCollection<Document> prices =
//                this.mongoClient
//                    .getDatabase(priceSource.name())
//                    .getCollection(PRICES_COLLECTION_NAME); //TODO set max size
//            prices.createIndex(
//                Indexes.ascending("price_zone", "price_day", "price_hour"),
//                new IndexOptions().name("price_idx_1").unique(true));
//            pricesCollections.put(priceSource, prices);
//            if (log.isTraceEnabled()) {
//              log.trace("Added MongoDB collection for PriceSource: {}", priceSource.name());
//            }
//          });
//    } catch (Exception e) {
//      log.error("Exception initializing collections in MongoDB: {} Message: {}",
//          e.getClass().getSimpleName(),
//          e.getMessage());
//    }
//
//  }

//  public Optional<PriceForHour> findPriceByPriceZoneAndPriceDayAndPriceHour(
//      final PriceSource priceSource,
//      final PriceZone priceZone,
//      final String priceDay,
//      final String priceHour) {
//
//    return Optional.ofNullable(
//        this.mongoClient
//            .getDatabase(priceSource.name())
//            .getCollection(PRICES_COLLECTION_NAME, PriceForHour.class)
//            .find(Filters.and(
//                Filters.eq("price_zone", priceZone.name()),
//                Filters.eq("price_day", priceDay),
//                Filters.eq("price_hour", priceHour)
//            )).first());
//
//
//  }

//  public void save(PriceForHour priceForHour) {
//
//    super.save(
//        pricesCollections.get(priceForHour.getPriceSource()),
//        priceForHour);
//
//  }

  /**
   * Deletes obsolete prices from the database
   */
//  @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24 hours in milliseconds
//  public void cleanUpDatabase() {
//
//    final long twoDaysAgo = System.currentTimeMillis() - 48 * 60 * 60 * 1000;
//    final var filter = Filters.lt("price_time_ms", twoDaysAgo);
//
//    pricesCollections.entrySet().stream()
//        .forEach(entry -> {
//          DeleteResult deleteResult = entry.getValue().deleteMany(filter);
//          if (log.isTraceEnabled()) {
//            log.trace(
//                "Deleted {} old prices from source {}",
//                deleteResult.getDeletedCount(),
//                entry.getKey().name());
//          }
//        });
//  }
//
//  public Map<PriceSource, List<PriceForHour>> findAll(PriceSource filterPriceSource) {
//
//    List<PriceSource> priceSources =
//        filterPriceSource == null ?
//            Arrays.asList(PriceSource.values()) :
//            List.of(filterPriceSource);
//
//    return priceSources.stream()
//        .collect(Collectors.toMap(
//            priceSource -> priceSource, // Key: PriceSource
//            source -> this.pricesCollections.get(source)
//                .find()
//                .map(this::convertToPriceForHour)
//                .into(new ArrayList<>())
//        ));
//
//  }

  private PriceForHour convertToPriceForHour(Document doc) {
    return PriceForHour.builder()
        .priceSource(PriceSource.valueOf(doc.getString("price_source"))) // Enum mapping
        .priceZone(PriceZone.valueOf(doc.getString("price_zone"))) // Enum mapping
        .sekPerKWh(doc.getDouble("sek_per_kwh"))
        .eurPerKWh(doc.getDouble("eur_per_kwh"))
        .exchangeRate(doc.getDouble("exchange_rate"))
        .priceTimeMs(doc.getLong("price_time_ms")) // Milliseconds timestamp
        .priceDay(doc.getString("price_day"))
        .priceHour(doc.getString("price_hour"))
        .priceTimeZone(doc.getString("price_time_zone"))
        .build();
  }


}
