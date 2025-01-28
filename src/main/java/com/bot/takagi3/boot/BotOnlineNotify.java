package com.bot.takagi3.boot;

import com.alibaba.fastjson2.JSONObject;
import com.bot.takagi3.common.constant.BotMsgConstant;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.util.CommonUtil;
import com.bot.takagi3.util.HttpUtil;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.enums.ActionPathEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;


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
        if(botProperties.getOnlineNotify())
        {
            String onlineMsg = String.format("%s Takagi3-Bot now is online.", CommonUtil.getFormattedLocalTime());
            log.info(onlineMsg);
            botProperties.getServeGroups().forEach(g -> bot.sendGroupMsg(g, onlineMsg, false));
        }
    }

    @Override
    public void offline(long account)
    {
        if(botProperties.getOfflineNotify())
        {
            String offlineMsg = String.format("%s Takagi3-Bot now is offline.", CommonUtil.getFormattedLocalTime());
            log.info(offlineMsg);

            botProperties.getServeGroups().forEach(g -> sendOfflineMsg(offlineMsg, g));
        }
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

        String result = HttpUtil.asyncPost(botProperties.getPostUrl() + ActionPathEnum.SEND_GROUP_MSG.getPath(), params.toJSONString(), 20);
        JSONObject resultMap = JSONObject.parseObject(result);
        if(resultMap.get("status").equals(BotMsgConstant.RESP_SUCCESS))
        {
            log.info("Bot send offline message to group:{} success.", groupId);
        }
        else
        {
            log.info("Bot send offline message to group:{} failed: {}", groupId, resultMap.get("message"));
        }
    }
}
