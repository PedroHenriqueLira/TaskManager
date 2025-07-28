package com.project.tasks.api.configs.log;

import com.project.tasks.api.enums.LogType;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogApp {

    private static final String LOG_DIR = "LOG";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static Environment env;

    public static void init(Environment environment) {
        env = environment;
    }

    public static void log(String module, LogType type, String message) {
        if (env == null) return;

        String propertyKey = "LOG_" + module.toUpperCase();
        String enabled = env.getProperty(propertyKey);

        if (!"1".equals(enabled)) return;

        String timestamp = TIME_FORMAT.format(LocalDateTime.now());
        String logLine = timestamp + " [" + type + "] " + message;

        try {
            Path dir = Path.of(LOG_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String fileName = String.format("LOG_%s_%s.log",
                    module.toUpperCase(), DATE_FORMAT.format(LocalDate.now()));
            Path logFile = dir.resolve(fileName);

            Files.writeString(logFile, logLine + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.err.println("Erro ao escrever log: " + e.getMessage());
        }
    }
}
