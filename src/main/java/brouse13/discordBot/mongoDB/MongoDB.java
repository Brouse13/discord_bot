package brouse13.discordBot.mongoDB;

import brouse13.discordBot.Main;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

public class MongoDB {

    static MongoClient mongoClient;

    protected static MongoDatabase database; //DiscordBot to DataBase

    protected static MongoCollection<Document> exp;
    protected static MongoCollection<Document> punishments;

    public void connect() {
        ConnectionString connString = new ConnectionString(
                System.getenv(MONGO_DB)
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();

        mongoClient = MongoClients.create(settings);

        database = mongoClient.getDatabase("DiscordBot");

        exp = database.getCollection("exp");
        punishments = database.getCollection("punishments");

        Main.log.insertLogLine("Conexión a DiscordBot establecida", this.getClass());
    }
}
