package com.bot.takagi3.boot;

import com.bot.takagi3.config.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableWebSocket
@EnableScheduling
@EnableTransactionManagement
@ComponentScan("com.bot.takagi3")
public class app
{

    public static void main(String[] args)
    {
        SpringApplication bot = new SpringApplication(app.class);
        bot.addInitializers(new BotConfiguration());
        bot.run(args);
    }

}
