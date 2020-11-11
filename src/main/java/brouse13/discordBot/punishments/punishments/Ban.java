package brouse13.discordBot.punishments.punishments;

import brouse13.discordBot.Main;
import brouse13.discordBot.punishments.Punishment;
import brouse13.discordBot.punishments.PunishmentType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Ban extends Punishment {
    public Ban(User author, User target, String reason, int time) {
        super(author, target, reason, time);
    }


    @Override
    public PunishmentType type() {
        return PunishmentType.BAN;
    }

    @Override
    public void executeAction(GuildMessageReceivedEvent event) {
        event.getGuild().ban(target, time, reason).queue();        event.getChannel().sendMessage(author.getAsMention()+ " baneó a "+ target.getAsTag()+ " durante "+ time+ " con motivo "+ reason);
    }

    @Override
    public void logAction() {
        Main.log.insertLogLine(author.getAsTag()+ " baneó a "+  target.getAsTag()+ " durante "+ time+ "d con motivo "+ reason, this.getClass());
    }

}
