package com.bot.takagi3.service;

import com.bot.takagi3.model.GptMsg;
import com.bot.takagi3.model.RandomAnimeImg;
import com.mikuac.shiro.common.utils.OneBotMedia;

import java.util.List;

public interface HttpRequestService
{
    RandomAnimeImg postForRandomAnimeImg(String tag, int isR18);

    String postForGptResponse(GptMsg msg, Long userId);
}
