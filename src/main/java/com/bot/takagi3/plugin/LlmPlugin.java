package com.bot.takagi3.plugin;

import com.bot.takagi3.common.enumeration.LlmTypeEnum;
import com.bot.takagi3.model.LlmMsg;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.service.HttpRequestService;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Component
@Shiro
@Slf4j
public class LlmPlugin
{
    private BotProperties botProperties;
    private HttpRequestService httpRequestService;

    @Autowired
    public LlmPlugin(HttpRequestService httpRequestService, BotProperties botProperties)
    {
        this.botProperties = botProperties;
        this.httpRequestService = httpRequestService;
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)GPT(\\s+.+)$", at = AtEnum.NEED)
    public void sendGptMsg(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String text = matcher.group(1).trim();
        String gptResp = httpRequestService.generateLlmResponse(text, event.getUserId(), LlmTypeEnum.GPT_4O_MINI);

        gptResp = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(gptResp)
                .build();
        bot.sendGroupMsg(event.getGroupId(), gptResp, false);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)Db(\\s+.+)$", at = AtEnum.NEED)
    public void sendDouBaoMsg(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String text = matcher.group(1).trim();
        String dbResp = httpRequestService.generateLlmResponse(text, event.getUserId(), LlmTypeEnum.DOU_BAO);

        dbResp = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(dbResp)
                .build();
        bot.sendGroupMsg(event.getGroupId(), dbResp, false);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)Dk(\\s+.+)$", at = AtEnum.NEED)
    public void sendDeepSeekMsg(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String text = matcher.group(1).trim();
        String dbResp = httpRequestService.generateLlmResponse(text, event.getUserId(), LlmTypeEnum.DEEP_SEEK);

        dbResp = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(dbResp)
                .build();
        bot.sendGroupMsg(event.getGroupId(), dbResp, false);
    }
}
