package se.fpcs.elpris.onoff.price;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.fpcs.elpris.onoff.Constants;
import se.fpcs.elpris.onoff.MongoDbService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Profile("!test")
@Log4j2
public class PriceService extends MongoDbService {

    private static final String PRICES_COLLECTION_NAME = "prices";

    private final ObjectMapper objectMapper;

    @SuppressWarnings("java:S1640")
    Map<PriceSource, MongoCollection<Document>> pricesCollections = new HashMap<>();

    public PriceService() {

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.setTimeZone(Constants.defaultTimeZone);
        this.objectMapper = om;

    }

    @PostConstruct
    @Override
    protected void initMongoDB() {

        super.initMongoDB();

        try {
            Arrays.stream(PriceSource.values())
                    .forEach(priceSource -> {
                        MongoCollection<Document> prices =
                                this.mongoClient
                                        .getDatabase(priceSource.name())
                                        .getCollection(PRICES_COLLECTION_NAME); //TODO set max size
                        prices.createIndex(
                                Indexes.ascending("price_zone", "price_day", "price_hour"),
                                new IndexOptions().name("price_idx_1").unique(true));
                        pricesCollections.put(priceSource, prices);
                        if (log.isTraceEnabled()) {
                            log.trace("Added MongoDB collection for PriceSource: {}", priceSource.name());
                        }
                    });
        } catch (Exception e) {
            log.error("Exception initializing collections in MongoDB: {} Message: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }

    }

    public Optional<PriceForHour> findPriceByPriceZoneAndPriceDayAndPriceHour(
            final PriceSource priceSource,
            final PriceZone priceZone,
            final String priceDay,
            final String priceHour) {

        return Optional.ofNullable(
                this.mongoClient
                        .getDatabase(priceSource.name())
                        .getCollection(PRICES_COLLECTION_NAME, PriceForHour.class)
                        .find(Filters.and(
                                Filters.eq("price_zone", priceZone.name()),
                                Filters.eq("price_day", priceDay),
                                Filters.eq("price_hour", priceHour)
                        )).first());


    }

    public void save(PriceForHour priceForHour) {

        try {
            pricesCollections.get(priceForHour.getPriceSource())
                    .insertOne(Document.parse(
                            objectMapper.writeValueAsString(priceForHour)));

        } catch (MongoWriteException e) {
            if (e.getMessage().contains("E11000")) {
                if (log.isTraceEnabled()) {
                    log.trace("Document already exist in collection");
                }
            } else {
                log.error("Error writing document: {}", e.getMessage());
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing instance to JSON: {} Message: {}", priceForHour, e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

    }

    /**
     * Deletes obsolete prices from the database
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24 hours in milliseconds
    public void cleanUpDatabase() {

        final long twoDaysAgo = System.currentTimeMillis() - 48 * 60 * 60 * 1000;
        final var filter = Filters.lt("price_time_ms", twoDaysAgo);

        pricesCollections.entrySet().stream()
                .forEach(entry -> {
                    DeleteResult deleteResult = entry.getValue().deleteMany(filter);
                    if (log.isTraceEnabled()) {
                        log.trace(
                                "Deleted {} old prices from source {}",
                                deleteResult.getDeletedCount(),
                                entry.getKey().name());
                    }
                });
    }

    public List<PriceForHour> findAll() {
        return List.of(); //TODO implement
    }

}
