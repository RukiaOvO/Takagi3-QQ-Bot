package com.bot.takagi3.service;

import com.bot.takagi3.model.RandomAnimeImg;
import com.bot.takagi3.model.LlmMsg;

public interface HttpRequestService {
    RandomAnimeImg postForRandomAnimeImg(String tag, int isR18);

    String postForGptResponse(LlmMsg msg, Long userId);

    String postForDoubaoResponse(LlmMsg msg, Long userId);
}
