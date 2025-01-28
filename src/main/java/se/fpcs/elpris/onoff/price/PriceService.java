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
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import se.fpcs.elpris.onoff.Constants;

import static java.util.Objects.requireNonNull;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service
@Profile("!test")
@Log4j2
public class PriceService {

    private static final String MONGODB_CONNECTION_STRING_NAME = "MONGODB_CONNECTION_STRING";
    private static final String MONGODB_CONNECTION_STRING = System.getenv(MONGODB_CONNECTION_STRING_NAME);
    private static final String PRICES_COLLECTION_NAME = "prices";

    private final ObjectMapper objectMapper;

    private final MongoClientSettings mongoClientSettings;
    private MongoClient mongoClient;

    @SuppressWarnings("java:S1640")
    Map<PriceSource, MongoCollection<Document>> pricesCollections = new HashMap<>();

    public List<PriceForHour> findAll() {
        return List.of(); //TODO implement
    }

    public PriceService() {

        requireNonNull(MONGODB_CONNECTION_STRING,
                "Environment variable " + MONGODB_CONNECTION_STRING_NAME + " not set");

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.setTimeZone(Constants.defaultTimeZone);
        this.objectMapper = om;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        this.mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGODB_CONNECTION_STRING))
                .serverApi(serverApi)
                .codecRegistry(pojoCodecRegistry)
                .build();

    }

    @PostConstruct
    public void initialize() {
        initMongoDB();
    }

    private void initMongoDB() {

        log.trace("Initializing MongoDB connection");

        try {
            this.mongoClient = MongoClients.create(mongoClientSettings);
            log.trace("Created mongoClient:{}", mongoClient);
        } catch (Exception e) {
            log.error("Exception creating mongoClient: {} Message: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }

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
                        log.trace("Added MongoDB collection for PriceSource: {}", priceSource.name());
                    });
        } catch (Exception e) {
            log.error("Exception initializing collections in MongoDB: {} Message: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }

        try {
            mongoClient.getDatabase("admin")
                    .runCommand(new Document("ping", 1));
            log.info("Successfully connected to MongoDB");
        } catch (Exception e) {
            log.error("Exception pinging MongoDB: {} Message: {}",
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
                log.trace("Document already exist in collection");
            } else {
                log.error("Error writing document: {}", e.getMessage());
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing instance to JSON: {} Message: {}", priceForHour, e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

    }


}
