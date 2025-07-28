package com.project.tasks.api.configs.log;

import com.project.tasks.api.enums.LogType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LogInitializer implements CommandLineRunner {
    private final Environment env;

    public LogInitializer(Environment env) {
        this.env = env;
    }

    @Override
    public void run(String... args) {
        LogApp.init(env);
        LogApp.log("APP", LogType.INFO, "Início da aplicação.");
    }
}
