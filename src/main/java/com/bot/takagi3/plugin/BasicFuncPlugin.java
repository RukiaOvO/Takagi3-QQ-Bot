package com.bot.takagi3.plugin;

import com.bot.takagi3.constant.BotMsgConstant;
import com.bot.takagi3.constant.CommonConstant;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.util.CommonUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Shiro
@Component
@Slf4j
public class BasicFuncPlugin
{
    private final BotProperties botProperties;

    public BasicFuncPlugin(BotProperties botProperties) {
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
    @MessageHandlerFilter(cmd = "^禁言\\s*\\[CQ:at,qq=(\\d+).*?\\]", at = AtEnum.NEED)
    public void setGroupBan(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String replyMsg;
        Long targetId = Long.parseLong(matcher.group(1));
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(event.getGroupId(), targetId, false);
        if(groupMemberInfo.getData() != null && groupMemberInfo.getStatus().equals(BotMsgConstant.RESP_SUCCESS))
        {
            targetId = groupMemberInfo.getData().getUserId();
            replyMsg = targetId.equals(event.getUserId()) ? BotMsgConstant.BAN_YOURSELF : BotMsgConstant.BAN_GROUP_MEMBER + groupMemberInfo.getData().getNickname();
        }
        else
        {
            targetId = event.getUserId();
            replyMsg = BotMsgConstant.BAN_YOURSELF;
        }

        log.info("User:{} ban {}", event.getUserId(), targetId);
        ActionRaw result = bot.setGroupBan(event.getGroupId(), targetId, CommonConstant.BAN_LIMIT);
        replyMsg = result.getStatus().equals(BotMsgConstant.RESP_SUCCESS) ? replyMsg : BotMsgConstant.BAN_FAILURE;

        replyMsg = MsgUtils.builder()
                .at(event.getUserId())
                .reply(event.getMessageId())
                .text(replyMsg)
                .build();
        bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
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
    @MessageHandlerFilter(cmd = "^翻译(\\s+[\\p{L}]+)(\\s+[\\u4e00-\\u9fa5]+)?$", at = AtEnum.NEED)
    public void translateMsg(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String textMsg = matcher.group(1);
        String targetLanguage = matcher.group(2) == null ? "chinese" : matcher.group(2);
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^公告(\\s+.*)$", at = AtEnum.NEED)
    public void publishGroupNotice(Bot bot, GroupMessageEvent event, Matcher matcher)
    {
        String content = matcher.group(1);

        log.info("User:{} sendGroupNotice:{}", event.getUserId(), content);
        ActionRaw result = bot.sendGroupNotice(event.getGroupId(), content);
        if(!result.getStatus().equals(BotMsgConstant.RESP_SUCCESS))
        {
            String replyMsg = MsgUtils.builder().at(event.getUserId()).reply(event.getMessageId()).text(BotMsgConstant.SEND_GROUP_NOTICE_FAILURE).build();
            bot.sendGroupMsg(event.getGroupId(), replyMsg, false);
        }
    }
}
