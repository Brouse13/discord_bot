package brouse13.discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class onlineCmd extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("!online")) {
            event.getTextChannel().sendTyping();
            try {
                JSONObject json = json = readJsonFromUrl("https://api.mcsrvstat.us/2/jugar.dragonballcreativemc.com");
                String online = json.getBoolean("online") ? "Sí" : "No";
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("DragonBallCreativeMC", "")
                        .setColor(Color.RED)
                        .addField("Ip: ", "jugar.dragonballcreativemc.com", false)
                        .addField("Online:", online, false);

                if (json.getBoolean("online")) {
                    eb.setColor(Color.GREEN)
                            .addField("Jugadores:",
                                    json.getJSONObject("players").get("online")+ "/"+ json.getJSONObject("players").get("max"),
                                    false);
                }
                event.getTextChannel().sendMessage(eb.build()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
