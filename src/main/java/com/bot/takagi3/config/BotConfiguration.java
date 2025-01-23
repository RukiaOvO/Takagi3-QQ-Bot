package com.bot.takagi3.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
public class BotConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext)
    {
    }
}