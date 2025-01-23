package com.bot.takagi3.boot;

import com.bot.takagi3.config.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class app
{

    public static void main(String[] args)
    {
        SpringApplication bot = new SpringApplication(app.class);
        bot.addInitializers(new BotConfiguration());
        bot.run(args);
    }

}
