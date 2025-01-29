package com.bot.takagi3.config;


import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.properties.MysqlProperties;
import com.bot.takagi3.util.LevelDBSingleton;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
@Slf4j
public class LevelDBConfiguration {

    private final BotProperties botProperties;

    @Autowired
    public LevelDBConfiguration(BotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @PostConstruct
    private void initLevelDB() {

        log.info("初始化 LevelDB...");

        try {
            if (!botProperties.getDataFolder().exists()) botProperties.getDataFolder().mkdir();
            LevelDBSingleton.init(Iq80DBFactory.factory.open(new File(botProperties.getDataFolder(), "database"),
                    new Options().createIfMissing(true)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @PreDestroy
    private void shutdownLevelDB() {
        try {
            LevelDBSingleton.closeDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
