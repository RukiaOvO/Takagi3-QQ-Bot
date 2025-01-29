package com.bot.takagi3.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bot.takagi3.common.constant.*;
import com.bot.takagi3.common.enumeration.LlmTypeEnum;
import com.bot.takagi3.config.LevelDBConfiguration;
import com.bot.takagi3.model.LlmMsg;
import com.bot.takagi3.model.RandomAnimeImg;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.service.HttpRequestService;
import com.bot.takagi3.util.HttpUtil;
import com.bot.takagi3.util.LevelDBSingleton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.constant.ActionParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HttpRequestServiceImpl implements HttpRequestService {
    private final BotProperties botProperties;

    @Autowired
    public HttpRequestServiceImpl(BotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @Override
    public RandomAnimeImg postForRandomAnimeImg(String tag, int isR18) {
        JSONObject params = new JSONObject();
        params.put("tag", tag);
        params.put("r18", isR18);
        params.put("num", 1);
        String result = HttpUtil.asyncPost(RequestParamConstant.RANDOM_ANIME_IMG_URL, params.toJSONString(), 10);

        JSONArray imgDataArray = JSONObject.parseObject(result).getJSONArray("data");
        JSONObject imgData = imgDataArray.getJSONObject(0);
        if (imgData == null || imgData.isEmpty()) {
            return null;
        }
        JSONObject urls = imgData.getJSONObject("urls");
        if (urls == null || urls.isEmpty()) {
            return null;
        }

        OneBotMedia img = OneBotMedia.builder().file((String) urls.get("original")).cache(false);
        return RandomAnimeImg.builder()
                .title((String) imgData.get("title"))
                .isR18(isR18 == 1)
                .img(img)
                .url((String) urls.get("original"))
                .build();
    }


    @Override
    public String generateLlmResponse(String msg, Long userId, LlmTypeEnum llmType) {
        List<LlmMsg> msgList = new ArrayList<>();

        String cacheKey = LlmConstant.LLM_MSG_CACHE_PREFIX + llmType.getPath() + userId;

        byte[] bytes = LevelDBSingleton.INSTANCE.get(cacheKey);
        if (msg.equals(LlmConstant.LLM_MEM_RELOAD_ORDER)) {
            LevelDBSingleton.INSTANCE.delete(cacheKey);
            return cacheKey + LlmConstant.LLM_MEM_RELOAD_RESP;
        }
        if (bytes != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                msgList = objectMapper.readValue(bytes, objectMapper.getTypeFactory().constructCollectionType(List.class, LlmMsg.class));
            } catch (IOException e) {
                log.error(CommonConstant.CACHE_DESERIALIZATION_ERROR + "{}", e.getMessage());
            }
        } else {
            msgList.add(new LlmMsg(LlmConstant.LLM_SYSTEM, LlmConstant.LLM_INIT_MSG));
        }
        msgList.add(new LlmMsg(LlmConstant.LLM_USER, msg));

        String result = questLlm(msgList, llmType);

        JSONArray choices = JSONObject.parseObject(result).getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            return BotMsgConstant.SEND_LLM_REQUEST_FAILURE;
        }
        LlmMsg respMsg = JSONObject.parseObject(((JSONObject) choices.get(0)).getJSONObject("message").toJSONString(), LlmMsg.class);

        if (respMsg == null) {
            return BotMsgConstant.SEND_LLM_REQUEST_FAILURE;
        } else {
            msgList.add(respMsg);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                LevelDBSingleton.INSTANCE.set(cacheKey, objectMapper.writeValueAsBytes(msgList));
            } catch (JsonProcessingException e) {
                log.error(CommonConstant.CACHE_SERIALIZATION_ERROR + "{}", e.getMessage());
            }
        }
        return respMsg.getContent();
    }

    private String questLlm(List<LlmMsg> msgList, LlmTypeEnum type)
    {
        String model;
        String apiKey;
        String llmUrl;

        switch(type)
        {
            case DOU_BAO -> {
                model = botProperties.getDouBaoModel();
                apiKey = botProperties.getDouBaoApiKey();
                llmUrl = LlmConstant.DOU_BAO_URL;
            }
            case DEEP_SEEK -> {
                model = botProperties.getDeepSeekModel();
                apiKey = botProperties.getDeepSeekApiKey();
                llmUrl = LlmConstant.DEEP_SEEK_URL;
            }
            default -> {
                model = botProperties.getGptModel();
                apiKey = botProperties.getGptApiKey();
                llmUrl = LlmConstant.GPT_URL;
            }
        }

        JSONObject params = new JSONObject();
        params.put("model", model);
        params.put("messages", msgList);

        return HttpUtil.asyncPost(llmUrl, params.toJSONString(), "Bearer " + apiKey, LlmConstant.LLM_REQUEST_TIMEOUT);
    }
}
