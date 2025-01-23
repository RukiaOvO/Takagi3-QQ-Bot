package com.bot.takagi3.plugin;

import com.alibaba.fastjson2.JSONObject;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.util.CommonUtil;
import com.bot.takagi3.util.HttpUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.enums.ActionPathEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.swing.*;
import java.util.List;


@Primary
@Component
@Slf4j
public class BotOnlineNotify extends CoreEvent
{
    @Autowired
    private BotProperties botProperties;

    @Override
    public void online(Bot bot)
    {
        String onlineMsg = String.format("%s Takagi3-Bot:%s now is online.", CommonUtil.getFormattedLocalTime(), bot.getSelfId());
        log.info(onlineMsg);
        botProperties.getServeGroups().forEach(g -> bot.sendGroupMsg(g, onlineMsg, false));
    }

    @Override
    public void offline(long account)
    {
        String offlineMsg = String.format("%s Takagi3-Bot:%s now is offline.", CommonUtil.getFormattedLocalTime(), account);
        log.info(offlineMsg);

        botProperties.getServeGroups().forEach(g -> sendOfflineMsg(offlineMsg, g));
    }

    @Override
    public boolean session(WebSocketSession session)
    {
        return true;
    }

    private void sendOfflineMsg(String msg, Long groupId)
    {
        JSONObject params = new JSONObject();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, false);

        HttpUtil.asyncPost(botProperties.getPostUrl() + ActionPathEnum.SEND_GROUP_MSG.getPath(), params.toJSONString(), 20);
    }
}
