package com.bot.takagi3.plugin;

import com.alibaba.fastjson2.JSONObject;
import com.bot.takagi3.constant.BotMsgConstant;
import com.bot.takagi3.constant.RequestParamConstant;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.util.HttpUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.GroupPokeNoticeHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Shiro
@Component
@Slf4j
public class AutoReplyPlugin
{
    @GroupMessageHandler
    public void replyGroupAtAll(Bot bot, GroupMessageEvent event)
    {
        if(!ShiroUtils.isAtAll(event.getMessage())){return;}

        log.info("Bot:{} reply atAllMsg.", bot.getSelfId());
        String replyMsg = MsgUtils.builder()
                                .text(BotMsgConstant.REPLY_AT_ALL)
                                .build();
        bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
    }

    @GroupPokeNoticeHandler
    public void replyGroupPokeNotice(Bot bot, PokeNoticeEvent event)
    {
        if(!event.getSelfId().equals(event.getTargetId())) {return;}

        log.info("Bot:{} reply pokeNotice.", bot.getSelfId());
        String replyMsg = MsgUtils.builder()
                                .text(BotMsgConstant.REPLY_POKE)
                                .build();
        bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
    }
}
