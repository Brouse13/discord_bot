package brouse13.discordBot.log;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class LogManager {
    JDA jda;
    Guild guild;

    public LogManager(JDA jda) {
        this.jda = jda;
        createLogChannel();
    }

    private void createLogChannel(){
        guild = jda.getGuildById(735153454456111184L);
        List<TextChannel> textChannels = guild.getTextChannels();
        List<String> nameList = new ArrayList<>();

        textChannels.forEach(textChannel -> nameList.add(textChannel.getName()));

        if (!nameList.contains("log")) {
            guild.createTextChannel("log").queue();
        }
    }

    public void sendLog(EmbedBuilder builder) {
        guild.getTextChannels().forEach(textChannel -> {
            if (textChannel.getName().equals("log")) {
                textChannel.sendTyping().queue();
                textChannel.sendMessage(builder.build()).queue();
            }
        });
        //Futuras actualizaciones poner log en un .txt
    }
}
