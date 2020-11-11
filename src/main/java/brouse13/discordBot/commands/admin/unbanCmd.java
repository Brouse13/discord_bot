package brouse13.discordBot.commands.admin;

import brouse13.discordBot.Main;
import brouse13.discordBot.log.LogManager;
import brouse13.discordBot.mongoDB.functions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class unbanCmd extends ListenerAdapter {
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            LogManager log = new LogManager(event.getJDA());
            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

            String[] args = event.getMessage().getContentRaw().split(" ");
            String reason = "";
            User author = event.getAuthor();

            if(event.getMessage().getContentRaw().startsWith("!unban")) {
                if(args.length > 1) {

                    if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention()+ " no puedes usar este comando").queue();
                        return;
                    }

                    if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("No tengo suficientes permisos para ejecutar esta acción").queue();
                        return;
                    }

                    if (args.length >= 3) {
                        for (int i = 2; i < args.length; i++) {
                            reason += args[i]+ " ";
                        }
                    }

                    final String finalReason = reason;
                    event.getGuild().retrieveBanList().queue(bans -> {
                        List<User> users = bans.stream().filter(ban -> isValidUser(ban, args[1]))
                                .map(Guild.Ban::getUser).collect(Collectors.toList());


                        if (users.isEmpty()) {
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage("El usuario no está baneado").queue();
                            return;
                        }

                        User target = users.get(0);
                        event.getGuild().retrieveBan(target).queue(
                                success -> {
                                    event.getGuild().unban(target).queue();
                                    event.getChannel().sendTyping().queue();
                                    event.getChannel().sendMessage("El usuario `"+ target.getName()+ "` ha sido desbaneado exitosamente").queue();

                                    functions.reyectBan(target.getIdLong());
                                    log.sendLog(new EmbedBuilder()
                                            .setTitle("Usuario desbaneado")
                                            .setColor(Color.red)
                                            .addField("Usuario:", target.getAsTag(), false)
                                            .addField("Motivo:", finalReason, false)
                                            .addField("Ejecutor:", event.getAuthor().getName(), false)
                                            .addField("Hora:", dateformat.format(new Date(System.currentTimeMillis())), false));
                                    Main.log.insertLogLine(author.getAsTag()+ " desbaneó a "+ target.getAsTag()+ " con motivo "+ finalReason, this.getClass());//Log para el .txt
                                },
                                failure -> {
                                    event.getChannel().sendTyping().queue();
                                    event.getChannel().sendMessage("El usuario `"+ target.getName()+ "` no está baneado").queue();
                                }
                        );

                    });
                }else {
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage("!unban <@Usuario> [motivo]").queue();
                }
            }
        }
    }

    private boolean isValidUser(Guild.Ban ban, String arg) {
        User bannedUser = ban.getUser();

        return bannedUser.getAsTag().equals(arg) || bannedUser.getId().equals(arg);
    }
}
