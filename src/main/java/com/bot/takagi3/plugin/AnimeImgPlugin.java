package com.bot.takagi3.plugin;

import com.bot.takagi3.constant.BotMsgConstant;
import com.bot.takagi3.model.RandomAnimeImg;
import com.bot.takagi3.service.HttpRequestService;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Shiro
@Component
@Slf4j
public class AnimeImgPlugin
{
    @Autowired
    private HttpRequestService httpRequestService;

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^随机涩图(\\s*[\\u4e00-\\u9fa5]+)?(\\s+(?i)r18)?$", at = AtEnum.NEED)
    public void randomAnimeImg(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String replyMsg;
        String tag = matcher.group(1) == null ? "" : matcher.group(1);
        int isR18 = matcher.group(2) == null ? 0 : 1;

        log.info("User:{} get randomAnimeImg:{}", event.getUserId(), tag);
        RandomAnimeImg animeImg = httpRequestService.postForRandomAnimeImg(tag, isR18);
        if(animeImg == null)
        {
            replyMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .reply(event.getMessageId())
                    .text(BotMsgConstant.GET_ANIME_IMG_FAILURE)
                    .build();
        }
        else
        {
            replyMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .reply(event.getMessageId())
                    .img(animeImg.getImg())
                    .text(String.format("标题:%s\nIsR18:%s\n", animeImg.getTitle(), animeImg.getIsR18()))
                    .build();
        }
        ActionData<MsgId> result = bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
        if(result == null || !result.getStatus().equals(BotMsgConstant.RESP_SUCCESS))
        {
            replyMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .reply(event.getMessageId())
                    .text(String.format("标题:%s\nIsR18:%s\n", animeImg.getTitle(), animeImg.getIsR18()) + BotMsgConstant.SEND_IMG_ERROR + "\n" + animeImg.getUrl())
                    .build();
            bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
        }
    }
}
