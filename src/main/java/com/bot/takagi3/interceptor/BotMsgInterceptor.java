package com.bot.takagi3.interceptor;

import com.bot.takagi3.constant.MsgEventConstant;
import com.bot.takagi3.properties.BotProperties;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.exception.ShiroException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BotMsgInterceptor implements BotMessageEventInterceptor
{
    @Autowired
    private BotProperties botProperties;

    @Override
    public boolean preHandle(Bot bot, MessageEvent event) throws ShiroException
    {
        if(event.getMessageType().equals(MsgEventConstant.GROUP_TYPE))
        {
            Long groupId = ((GroupMessageEvent) event).getGroupId();
            Long userId = event.getUserId();
            return botProperties.getServeGroups().contains(groupId) && !botProperties.getUserBlackList().contains(userId);
        }
        else if(event.getMessageType().equals(MsgEventConstant.PRIVATE_TYPE))
        {
            return !botProperties.getUserBlackList().contains(event.getUserId());
        }
        else if(event.getMessageType().equals(MsgEventConstant.GUILD_TYPE))
        {
            return false;
        }
        return false;
    }

    @Override
    public void afterCompletion(Bot bot, MessageEvent event) throws ShiroException
    {
        log.info("Takagi3-Bot: {} completion event: {}", bot.getSelfId(), event.getMessageType() + ":" + event.getMessage());
    }
}
