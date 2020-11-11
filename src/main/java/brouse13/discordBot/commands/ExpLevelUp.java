package brouse13.discordBot.commands;

import brouse13.discordBot.mongoDB.MongoDB;
import brouse13.discordBot.mongoDB.functions;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Random;

public class ExpLevelUp extends ListenerAdapter {
    Random random = new Random();


    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            if (!event.getMessage().getContentRaw().startsWith("!")) {

                Document user = functions.getExp(event.getAuthor().getIdLong());

                if(user == null) {
                    functions.createExp(event.getAuthor().getIdLong());
                }

                int exp = user.getInteger("exp");
                int level = user.getInteger("level");
                int prox_level = (level*(level^3)+10);

                exp+=random.nextInt(3)+1;

                if(exp >= prox_level) {
                    level+=1;
                    exp-=prox_level;

                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention()+ " acaba de subir a nivel "+ level).queue();
                }

                Document updated_value = new Document("exp", exp)
                        .append("level", level);
                Document update_operation = new Document("$set", updated_value);

                functions.getExp().updateOne(user, update_operation);
            }
        }
    }
}
