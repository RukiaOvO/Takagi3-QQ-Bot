package com.bot.takagi3.plugin;

import com.bot.takagi3.common.constant.BotMsgConstant;
import com.bot.takagi3.common.constant.CommonConstant;
import com.bot.takagi3.common.constant.RequestParamConstant;
import com.bot.takagi3.common.enumeration.CustomActionPath;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.util.ArrayMsgUtil;
import com.bot.takagi3.util.CommonUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

@Shiro
@Component
@Slf4j
public class BasicFuncPlugin
{
    private final BotProperties botProperties;

    @Autowired
    public BasicFuncPlugin(BotProperties botProperties)
    {
        this.botProperties = botProperties;
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^点赞(\\s*[0-9]+)?", at = AtEnum.NEED)
    public void sendLike(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        int times = 10;
        String numStr = matcher.group(1);
        if(numStr != null)
        {
            times = Integer.parseInt(numStr.trim());
            times = (times < 1 || times > 10) ? 10 : times;
        }
        log.info("User:{} sendLike {} times.", event.getUserId(), times);
        ActionRaw actionResp= bot.sendLike(event.getUserId(), times);

        String replyMsg = actionResp.getStatus().equals(BotMsgConstant.RESP_SUCCESS) ?
                BotMsgConstant.LIKE_SUCCESS : BotMsgConstant.LIKE_FAILURE;

        replyMsg = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(replyMsg)
                .build();
        bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^头衔\\s(.*)?$", at = AtEnum.NEED)
    public void setSpecialTitle(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String title = matcher.group(1);
        log.info("User:{} setSpecialTitle:{}", event.getUserId(), title);

        ActionRaw actionResp = bot.setGroupSpecialTitle(event.getGroupId(), event.getUserId(), title, -1);
        String replyMsg = actionResp.getStatus().equals(BotMsgConstant.RESP_SUCCESS) ?
                BotMsgConstant.SET_SPECIAL_TITLE_SUCCESS : BotMsgConstant.SET_SPECIAL_TITLE_FAILURE;

        replyMsg = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(replyMsg + "[" + title + "]")
                .build();
        bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^禁言\\s*\\[CQ:at,qq=([0-9]+).*?(\\s*[0-9]+)?$", at = AtEnum.NEED)
    public void setGroupBan(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        long targetId = Long.parseLong(matcher.group(1));
        long time = matcher.group(2) == null ? 10 : Long.parseLong(matcher.group(2).trim());

        log.info("User:{} ban {} {} seconds.", event.getUserId(), targetId, time);
        ActionRaw result = bot.setGroupBan(event.getGroupId(), targetId, CommonConstant.BAN_LIMIT);
        if(!result.getStatus().equals(BotMsgConstant.RESP_SUCCESS))
        {
            String replyMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .reply(event.getMessageId())
                    .text(BotMsgConstant.BAN_FAILURE)
                    .build();
            bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
            return;
        }
        try
        {
            TimeUnit.SECONDS.sleep(time);
            bot.setGroupBan(event.getGroupId(), targetId, RequestParamConstant.UN_BAN);
        }
        catch(Exception e)
        {
            String replyMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .reply(event.getMessageId())
                    .text(e.getMessage())
                    .build();
            bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
        }
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^菜单$", at = AtEnum.NEED)
    public void getOrderMenu(Bot bot, GroupMessageEvent event)
    {
        log.info("User:{} getOrderMenu.", event.getUserId());

        String imgUrl = CommonUtil.getImgStaticUrlByName(CommonConstant.ORDER_MENU_IMG_NAME);
        OneBotMedia orderMenuImg = OneBotMedia.builder().file(imgUrl).cache(false);
        String replyMsg = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .img(orderMenuImg)
                .build();
        ActionData<MsgId> result = bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
        if(!result.getStatus().equals(BotMsgConstant.RESP_SUCCESS))
        {
            replyMsg = MsgUtils.builder()
                    .at(event.getUserId())
                    .reply(event.getMessageId())
                    .text(BotMsgConstant.GET_ORDER_MENU_FAILURE)
                    .build();
            bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
        }
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^公告(\\s+.*)$", at = AtEnum.NEED)
    public void publishGroupNotice(Bot bot, GroupMessageEvent event)
    {
        List<String> urlList = ShiroUtils.getMsgImgUrlList(event.getArrayMsg());
        String content = ArrayMsgUtil.getTextContent(event.getArrayMsg()).trim().replace("公告", "");

        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, event.getGroupId());
        params.put(ActionParams.CONTENT, content);
        params.put(ActionParams.IMAGE, (urlList == null || urlList.isEmpty()) ? "" : urlList.get(0));

        log.info("User:{} publishGroupNotice:{}", event.getUserId(), content);
        String result = bot.customRequest(CustomActionPath.SEND_GROUP_NOTICE, params).getStatus();
        result = result.equals(BotMsgConstant.RESP_SUCCESS) ? BotMsgConstant.SEND_GROUP_NOTICE_SUCCESS : BotMsgConstant.SEND_GROUP_NOTICE_FAILURE;
        result = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(result)
                .build();
        bot.sendGroupMsg(event.getGroupId(), result, false);
    }
}
