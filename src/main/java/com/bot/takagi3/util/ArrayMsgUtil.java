package com.bot.takagi3.util;

import com.bot.takagi3.common.constant.BotMsgConstant;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.List;

public class ArrayMsgUtil
{
    private ArrayMsgUtil(){}

    public static String getTextContent(List<ArrayMsg> msgList)
    {
        StringBuilder contentBuilder = new StringBuilder();
        msgList.forEach(msg -> {
             if(msg.getType().equals(MsgTypeEnum.text))
             {
                 contentBuilder.append(msg.getData().get("text"));
             }
        });

        return contentBuilder.toString();
    }
}
