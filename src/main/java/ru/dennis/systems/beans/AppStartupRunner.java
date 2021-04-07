package ru.dennis.systems.beans;

import ru.dennis.systems.db.Updater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppStartupRunner implements ApplicationRunner {

    private final Updater updater;

    public AppStartupRunner(Updater updater) {
        this.updater = updater;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application started with option names : {}",
                args.getOptionNames());
        log.info("Starting db Update... ");
        updater.updateDb();

    }
}