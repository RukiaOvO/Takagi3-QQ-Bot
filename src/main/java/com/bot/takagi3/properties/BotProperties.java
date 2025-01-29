package com.bot.takagi3.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "takagi3")
@Data
public class BotProperties {
    Boolean onlineNotify;

    Boolean offlineNotify;

    String postUrl;

    List<Long> serveGroups;

    List<Long> userBlackList;

    String gptModel;

    String gptApiKey;

    String gptRepostUrl;

    String douBaoApiKey;

    String douBaoModel;

    File dataFolder;
}
