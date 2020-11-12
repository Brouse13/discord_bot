package brouse13.discordBot;

import brouse13.discordBot.commands.ExpLevelUp;
import brouse13.discordBot.commands.admin.clearCmd;
import brouse13.discordBot.commands.admin.unbanCmd;
import brouse13.discordBot.commands.onlineCmd;
import brouse13.discordBot.commands.admin.banCmd;
import brouse13.discordBot.log.LogFile;
import brouse13.discordBot.mongoDB.MongoDB;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import javax.security.auth.login.LoginException;
import java.util.Scanner;


public class Main {
    public static LogFile log = new LogFile();
    private static MongoDB db = new MongoDB();

/**
*
 */
    public static void main(String[] args) throws LoginException, Exception {
        log.createLog();
        log.insertLogLine("Inicializando bot de discord", Main.class);
        JDA jda = JDABuilder.createDefault(System.getenv("TOKEN"))
                .setActivity(Activity.playing("DBC"))
                .setStatus(OnlineStatus.ONLINE)
                .build();

        log.insertLogLine("Inicializando base de datos", Main.class);
        db.connect();

        log.insertLogLine("Añadiendo eventos", Main.class);
        jda.addEventListener(new onlineCmd());
        jda.addEventListener(new clearCmd());
        jda.addEventListener(new ExpLevelUp());
        jda.addEventListener(new banCmd());
        jda.addEventListener(new unbanCmd());
        log.insertLogLine("Bot inicializado y listo para su uso", Main.class);


        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.next().equals("stop")) {
                jda.shutdown();
                log.insertLogLine("Bot apagado por consola", Main.class);
                System.exit(-1);
            }else {
                System.out.println("Comando no reconocido");
            }
        }


    }




}
