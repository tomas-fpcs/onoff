package se.fpcs.elpris.onoff;

import static java.util.Objects.requireNonNull;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

@Log4j2
public abstract class MongoDbService {

  private static final String MONGODB_CONNECTION_STRING_NAME = "MONGODB_CONNECTION_STRING";
  private static final String MONGODB_CONNECTION_STRING = System.getenv(
      MONGODB_CONNECTION_STRING_NAME);
  private final MongoClientSettings mongoClientSettings;

  private final ObjectMapper objectMapper;

  protected MongoClient mongoClient;

  public MongoDbService() {

    ObjectMapper om = new ObjectMapper();
    om.registerModule(new JavaTimeModule());
    om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    om.setTimeZone(Constants.defaultTimeZone);
    this.objectMapper = om;

    requireNonNull(MONGODB_CONNECTION_STRING,
        "Environment variable " + MONGODB_CONNECTION_STRING_NAME + " not set");

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

  protected void initMongoDB() {

    if (log.isTraceEnabled()) {
      log.trace("Initializing MongoDB connection");
    }

    try {
      this.mongoClient = MongoClients.create(mongoClientSettings);
      if (log.isTraceEnabled()) {
        log.trace("Created mongoClient:{}", mongoClient);
      }
    } catch (Exception e) {
      log.error("Exception creating mongoClient: {} Message: {}",
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

  protected void save(MongoCollection<Document> collection, Object obj) {

    try {
      collection.insertOne(Document.parse(
          objectMapper.writeValueAsString(obj)));

    } catch (MongoWriteException e) {
      if (e.getMessage().contains("E11000")) {
        if (log.isTraceEnabled()) {
          log.trace("Document already exist in collection");
        }
      } else {
        log.error("Error writing document: {}", e.getMessage());
      }
    } catch (JsonProcessingException e) {
      log.error("Error serializing instance to JSON. Message: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Exception: {}", e.getMessage());
    }

  }

}
