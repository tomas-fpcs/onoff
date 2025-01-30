package se.fpcs.elpris.onoff;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static java.util.Objects.requireNonNull;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Log4j2
public abstract class MongoDbService {

    private static final String MONGODB_CONNECTION_STRING_NAME = "MONGODB_CONNECTION_STRING";
    private static final String MONGODB_CONNECTION_STRING = System.getenv(MONGODB_CONNECTION_STRING_NAME);
    private final MongoClientSettings mongoClientSettings;

    protected MongoClient mongoClient;

    public MongoDbService() {

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

}
