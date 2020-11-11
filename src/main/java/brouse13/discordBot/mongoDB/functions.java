package brouse13.discordBot.mongoDB;

import brouse13.discordBot.punishments.PunishmentType;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class functions extends MongoDB{

    public static Document getExp(long id) {
        return exp.find(new Document("_id", id)).first();
    }

    public static Document getPunishments(long id) {
        return punishments.find(new Document("_id", id)).first();
    }

    public static void createExp(@NotNull long id) {
        Document exp = new Document("_id", id)
                .append("exp", 0)
                .append("level", 0);
        database.getCollection("exp").insertOne(exp);
    }

    public static void createPunisments(@NotNull long id) {
        Document punishments = new Document("_id", id)
                .append("warn", new ArrayList<String>())
                .append("kick", new ArrayList<String>())
                .append("mute", new ArrayList<String>())
                .append("ban", new ArrayList<String>());
        database.getCollection("punishments").insertOne(punishments);
    }

    public static void insertPunisment(@NotNull PunishmentType sancion, @NotNull User author, @NotNull User target, int amount, @NotNull String motivo) {
        Document user_punishments = MongoDB.punishments.find(new Document("_id", target.getIdLong())).first();
        switch (sancion) {
            case WARN:
                Document warnEntry = new Document()
                        .append("staff", author.getAsTag())
                        .append("motivo", motivo)
                        .append("hora",new Date(System.currentTimeMillis()));
                MongoDB.punishments.updateOne(eq("_id", target.getIdLong()), Updates.addToSet("warn", warnEntry));
                break;
            case KICK:
                Document kickEnty = new Document()
                        .append("staff", author.getAsTag())
                        .append("motivo", motivo)
                        .append("hora",new Date(System.currentTimeMillis()));
                MongoDB.punishments.updateOne(eq("_id", target.getIdLong()), Updates.addToSet("kick", kickEnty));
                break;
            case MUTE:
                Document muteEnty = new Document()
                        .append("staff", author.getAsTag())
                        .append("motivo", motivo)
                        .append("hora",new Date(System.currentTimeMillis()))
                        .append("tiempo", amount);
                MongoDB.punishments.updateOne(eq("_id", target.getIdLong()), Updates.addToSet("mute", muteEnty));
                break;
            case BAN:
                Document banEnty = new Document()
                        .append("staff", author.getAsTag())
                        .append("hora",new Date(System.currentTimeMillis()))
                        .append("motivo", motivo)
                        .append("tiempo", amount)
                        .append("baned", true);
                MongoDB.punishments.updateOne(eq("_id", target.getIdLong()), Updates.addToSet("ban", banEnty));
                break;
            case BLACKSLIST:
                //NOT ADDED YET
                break;
        }
    }

    public static void reyectBan(long id) {
        Document user_punishments = MongoDB.punishments.find(new Document("_id", id)).first();
        List<Document> banlist = (List<Document>) user_punishments.get("ban");

        for (Document ban : banlist) {
            if (ban.getBoolean("baned")) {
                banlist.remove(ban);
                Document banEnty = new Document()
                        .append("staff", ban.getString("staff"))
                        .append("hora", ban.getDate("hora"))
                        .append("motivo", ban.getString("motivo"))
                        .append("tiempo", ban.getInteger("tiempo"))
                        .append("baned", false);
                banlist.add(banEnty);
                MongoDB.punishments.updateOne(eq("_id", id), new Document("$set", new Document("ban", banlist)));
                break;
            }
        }
    }
    public static MongoDatabase getDatabase() {
        return database;
    }

    public static MongoCollection<Document> getExp() {
        return exp;
    }

    public static MongoCollection<Document> getPunishments() {
        return punishments;
    }
}
