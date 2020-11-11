package brouse13.discordBot.punishments;

import brouse13.discordBot.Main;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

public abstract class Punishment {

    public Punishment(@Nullable User author, @Nullable User target, @Nullable String reason, @Nullable int time) {
        this.target = target;
        this.author = author;
        this.reason = reason;
        this.time = time;
        logAction();
    }

    protected User target;
    protected User author;
    protected String reason;
    protected int time;
    protected abstract PunishmentType type();

    public abstract void executeAction(GuildMessageReceivedEvent event);

    public void logAction() {
        Main.log.insertLogLine("Please define a log message", super.getClass());
    }
}
