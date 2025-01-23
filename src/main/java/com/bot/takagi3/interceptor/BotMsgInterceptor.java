package com.bot.takagi3.interceptor;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.exception.ShiroException;

public class BotMsgInterceptor implements BotMessageEventInterceptor
{
    @Override
    public boolean preHandle(Bot bot, MessageEvent event) throws ShiroException
    {
        return false;
    }

    @Override
    public void afterCompletion(Bot bot, MessageEvent event) throws ShiroException
    {

    }
}
