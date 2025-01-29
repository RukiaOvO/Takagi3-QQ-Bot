package com.bot.takagi3.service;

import com.bot.takagi3.common.enumeration.LlmTypeEnum;
import com.bot.takagi3.model.RandomAnimeImg;
import com.bot.takagi3.model.LlmMsg;

import java.util.List;

public interface HttpRequestService {
    RandomAnimeImg postForRandomAnimeImg(String tag, int isR18);

    String generateLlmResponse(String msg, Long userId, LlmTypeEnum type);
}
