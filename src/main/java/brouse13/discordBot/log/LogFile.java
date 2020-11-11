package brouse13.discordBot.log;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogFile {
    private String current_dir = System.getProperty("user.dir");

    File directory = new File(current_dir.replace("/", "\\")+ "\\Log");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    File logFile;
    PrintStream console;

    public void createLog() {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        List<File> files = new ArrayList<>();
        List<String> matches = new ArrayList<>();
        Collections.addAll(files, directory.listFiles());

        files.stream()
                .filter(file -> file.getName().contains(dateFormat.format(System.currentTimeMillis())))
                .map(File::getName)
                .forEach(matches::add);

        if (matches.isEmpty()) {
            try {
                logFile = new File(directory, dateFormat.format(System.currentTimeMillis())+ ".txt");
                logFile.createNewFile();
            }catch (IOException ex) {
                System.out.println("No se pudo crear el archivo de log");
            }
        }else {
            for (int i = 1; i < matches.size()+1; i++) {
                try {
                    logFile = new File(directory, dateFormat.format(System.currentTimeMillis()) + " (" + i + ").txt");
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                        break;
                    }
                } catch (IOException exception) {
                    System.out.println("No se pudo crear el archivo de log");
                }
            }
        }
    }

    public void insertLogLine(String line, @Nullable Class classform) {
        try {
            console = System.out;
            System.setOut(new PrintStream(logFile));
            System.out.println(String.format("[%s] (%s): %s",classform.getName(), timeFormat.format(System.currentTimeMillis()), line));

            System.setOut(new PrintStream(console));
            System.out.println(String.format("[%s] (%s): %s",classform.getName(), timeFormat.format(System.currentTimeMillis()), line));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
