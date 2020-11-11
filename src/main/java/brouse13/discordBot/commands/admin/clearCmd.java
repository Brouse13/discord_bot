package brouse13.discordBot.commands.admin;

import brouse13.discordBot.Main;
import brouse13.discordBot.log.LogManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class clearCmd extends ListenerAdapter {
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            LogManager log = new LogManager(event.getJDA());
            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

            String[] args = event.getMessage().getContentRaw().split(" ");
            User author = event.getAuthor();
            TextChannel channel = event.getTextChannel();
            int amount;

            if (args[0].equals("!clear")) {
                if (args.length == 2) {
                    if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention()+ " no puesdes usar este comando").queue();
                        return;
                    }

                    if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("No tengo suficientes permisos para ejecutar esta acción").queue();
                        return;
                    }

                    try {
                        amount = Integer.parseInt(args[1]);
                    }catch (NumberFormatException exception) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("`"+ args[1]+ "` no es un número válido").queue();
                        return;
                    }

                    event.getChannel().getIterableHistory()
                            .takeAsync(amount)
                            .thenApplyAsync(messages -> {
                                event.getChannel().purgeMessages(messages);
                                return messages.size();
                            })
                            .whenCompleteAsync((count, throwable) -> {
                                event.getChannel().sendTyping().queue();
                                event.getChannel().sendMessage("Se eliminaron `"+ count+ "` mensajes")
                                            .queue(
                                                message -> message.delete().queueAfter(5, TimeUnit.SECONDS)
                                            );

                                log.sendLog(new EmbedBuilder()
                                        .setTitle("Mensajes eliminados")
                                        .setColor(Color.yellow)
                                        .addField("Autor:", author.getAsTag(), false)
                                        .addField("Cantidad:", count + "", false)
                                        .addField("Canal", "#"+ channel.getName(), false)
                                        .addField("Hora:", dateformat.format(new Date(System.currentTimeMillis())) + "", false));
                                Main.log.insertLogLine(author.getAsTag()+ " eliminó "+ amount+ " mensajes de "+ channel.getName(), this.getClass());
                            })

                            .exceptionally(throwable -> {
                                String cause = "Error: ";

                                if (throwable.getCause() != null) {
                                    cause += throwable.getCause().getMessage();
                                }

                                event.getChannel().sendTyping();
                                event.getChannel().sendMessage(cause).queue();
                                Main.log.insertLogLine("Error: "+ throwable.getMessage(), this.getClass());
                                return 0;
                            });
                }else {
                    event.getChannel().sendTyping();
                    event.getChannel().sendMessage("!clear <cantidad>");
                }
            }
        }
    }
}

