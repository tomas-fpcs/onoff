package se.fpcs.elpris.onoff.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.fpcs.elpris.onoff.MongoDbService;

@Service
@Profile("!test")
@Log4j2
public class UserService extends MongoDbService {

  private static final String ONOFF_DATABASE = "ONOFF";
  public static final String USERS_COLLECTION = "users";

  private MongoCollection<Document> users;


  @PostConstruct
  @Override
  protected void initMongoDB() {
    super.initMongoDB();

    try {
      this.users =
          this.mongoClient
              .getDatabase(ONOFF_DATABASE)
              .getCollection(USERS_COLLECTION);
      users.createIndex(
          Indexes.ascending("email"),
          new IndexOptions().name("email").unique(true));
      if (log.isTraceEnabled()) {
        log.trace("Added MongoDB collection for User");
      }

    } catch (Exception e) {
      log.error("Exception initializing collections in MongoDB: {} Message: {}",
          e.getClass().getSimpleName(),
          e.getMessage());
    }
  }

  public List<User> findAll() {

    return this.mongoClient
        .getDatabase(ONOFF_DATABASE)
        .getCollection(USERS_COLLECTION, User.class)
        .find()
        .into(new ArrayList<>());

  }

  public User createUser(User user) {

    save(users, user);
    return user;

  }
}
