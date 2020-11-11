package brouse13.discordBot.commands.admin;

import brouse13.discordBot.Main;
import brouse13.discordBot.log.LogManager;
import brouse13.discordBot.mongoDB.functions;
import brouse13.discordBot.punishments.PunishmentType;
import brouse13.discordBot.punishments.punishments.Ban;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class banCmd extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(!event.getAuthor().isBot()) {
            LogManager log = new LogManager(event.getJDA());
            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

            String reason = "";
            String[] args = event.getMessage().getContentRaw().split(" ");
            User author = event.getAuthor();
            User target;
            int amount;

            if(event.getMessage().getContentRaw().startsWith("!ban")) {
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
                    if (event.getMessage().getMentionedMembers().isEmpty()) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("Debes mencioanr al usuario").queue();
                        return;
                    }else {
                        target = event.getMessage().getMentionedMembers().get(0).getUser();
                    }

                    try {
                        amount = Integer.parseInt(args[2]);
                    }catch (NumberFormatException exception) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage(args[2]+ " no es un número válido").queue();
                        return;
                    }

                    if (args.length >= 4) {
                        for (int i = 3; i < args.length; i++) {
                            reason += args[i]+ " ";
                        }
                    }
                    new Ban(target, author, reason, amount).executeAction(event);

                    functions.insertPunisment(PunishmentType.BAN, author, target, amount, reason);

                    log.sendLog(new EmbedBuilder()//Log para el canal #log
                            .setTitle("Usuario baneado")
                            .setColor(Color.red)
                            .addField("Usuario:", target.getAsTag(), false)
                            .addField("Motivo:", reason, false)
                            .addField("Ejecutor:", author.getAsTag(), false)
                            .addField("Hora:", dateformat.format(new Date(System.currentTimeMillis())), false)
                    );
                    Main.log.insertLogLine(author.getAsTag()+ " baneó a "+ target.getAsTag()+ " con motivo "+ reason, this.getClass());//Log para el .txt
                }else{
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage("!ban <@Usuario> <dias> [motivo]").queue();
                }
            }
        }
    }
}
