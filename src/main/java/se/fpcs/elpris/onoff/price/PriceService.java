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

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service
@Profile("!test")
@Log4j2
public class PriceService {

    public static final String PRICES_COLLECTION = "prices";
    private final ObjectMapper objectMapper;
    private MongoClient mongoClient;

    @SuppressWarnings("java:S1640")
    Map<PriceSource, MongoCollection<Document>> pricesCollections = new HashMap<>();

    public List<PriceForHour> findAll() {
        return List.of(); //TODO implement
    }

    public PriceService() {

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.setTimeZone(Constants.defaultTimeZone);
        this.objectMapper = om;

        initMongoDB();
    }

    //TODO need to call again if connection fails??
    private void initMongoDB() {

        log.info("initMongoDB2() enter");
        try {
            final String connectionString = System.getenv("MONGODB_CONNECTION_STRING");
            log.info("MongoDB connectionString: {}", connectionString.substring(0, 9));

            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(serverApi)
                    .codecRegistry(pojoCodecRegistry)
                    .build();

            log.info("Creating mongoClient");
            this.mongoClient = MongoClients.create(settings);
            log.info("Created mongoClient:{}", mongoClient);

            Arrays.stream(PriceSource.values())
                    .forEach(priceSource -> {
                        MongoCollection<Document> prices =
                                this.mongoClient
                                        .getDatabase(priceSource.name())
                                        .getCollection(PRICES_COLLECTION); //TODO set max size
                        prices.createIndex(
                                Indexes.ascending("price_zone", "price_day", "price_hour"),
                                new IndexOptions().name("price_idx_1").unique(true));
                        log.info("Adding prices collection: {} for: {}", prices, priceSource);
                        pricesCollections.put(priceSource, prices);
                    });
        } catch (Exception e) {
            log.error("Exception initializing connection to MongoDB: {} Message: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }


        try {
            // Send a ping to confirm a successful connection
            MongoDatabase database = mongoClient.getDatabase("admin");
            database.runCommand(new Document("ping", 1));
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

        PriceForHour priceForHour = this.mongoClient
                .getDatabase(priceSource.name())
                .getCollection(PRICES_COLLECTION, PriceForHour.class)
                .find(Filters.and(
                        Filters.eq("price_zone", priceZone.name()),
                        Filters.eq("price_day", priceDay),
                        Filters.eq("price_hour", priceHour)
                )).first();
        return Optional.ofNullable(
                priceForHour);


    }

    public void save(PriceForHour priceForHour) {

        try {
            MongoCollection<Document> collection = pricesCollections.get(priceForHour.getPriceSource());
            log.info("2priceSource: {} collection: {}", priceForHour.getPriceSource(), collection);
            String json = objectMapper.writeValueAsString(priceForHour);
            log.info("json: {}", json);
            collection
                    .insertOne(Document.parse(
                            json));

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
