package com.bot.takagi3.service;

import com.bot.takagi3.model.RandomAnimeImg;
import com.mikuac.shiro.common.utils.OneBotMedia;

public interface HttpRequestService
{
    RandomAnimeImg postForRandomAnimeImg(String tag, int isR18);
}
