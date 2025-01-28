package com.bot.takagi3.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bot.takagi3.common.constant.BotMsgConstant;
import com.bot.takagi3.common.constant.CommonConstant;
import com.bot.takagi3.common.constant.RedisConstant;
import com.bot.takagi3.common.constant.RequestParamConstant;
import com.bot.takagi3.model.GptMsg;
import com.bot.takagi3.model.RandomAnimeImg;
import com.bot.takagi3.properties.BotProperties;
import com.bot.takagi3.service.HttpRequestService;
import com.bot.takagi3.util.HttpUtil;
import com.mikuac.shiro.common.utils.OneBotMedia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HttpRequestServiceImpl implements HttpRequestService
{
    private final BotProperties botProperties;
    private final RedisTemplate redisTemplate;

    @Autowired
    public HttpRequestServiceImpl(RedisTemplate redisTemplate, BotProperties botProperties)
    {
        this.botProperties = botProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RandomAnimeImg postForRandomAnimeImg(String tag, int isR18)
    {
        JSONObject params = new JSONObject();
        params.put("tag", tag);
        params.put("r18", isR18);
        params.put("num", 1);
        String result = HttpUtil.asyncPost(RequestParamConstant.RANDOM_ANIME_IMG_URL, params.toJSONString(), 10);

        JSONArray imgDataArray = JSONObject.parseObject(result).getJSONArray("data");
        JSONObject imgData =imgDataArray.getJSONObject(0);
        if(imgData == null || imgData.isEmpty()){return null;}
        JSONObject urls = imgData.getJSONObject("urls");
        if(urls == null || urls.isEmpty()){return null;}

        OneBotMedia img = OneBotMedia.builder().file((String)urls.get("original")).cache(false);
        return RandomAnimeImg.builder()
                        .title((String)imgData.get("title"))
                        .isR18(isR18 == 1)
                        .img(img)
                        .url((String)urls.get("original"))
                        .build();
    }

    @Override
    public String postForGptResponse(GptMsg msg, Long userId)
    {
        List<GptMsg> msgList = new ArrayList<>();
        String cacheKey = RedisConstant.GPT_MSG_CACHE_PREFIX + userId;
        GptMsg data = (GptMsg)redisTemplate.opsForValue().get(cacheKey);
        if(data != null) {msgList.add(data);}
        msgList.add(msg);

        JSONObject params = new JSONObject();
        params.put("model", botProperties.getGptModel());
        params.put("messages", msgList);
        String result = HttpUtil.asyncPost(botProperties.getGptRepostUrl(), params.toJSONString(), "Bearer " + botProperties.getGptApiKey(), 20);

        String gptModelType = (String)JSONObject.parseObject(result).get("model");
        if(gptModelType == null || gptModelType.isEmpty()) {return BotMsgConstant.SEND_GPT_REQUEST_FAILURE;}

        JSONArray choices = JSONObject.parseObject(result).getJSONArray("choices");
        if(choices == null || choices.isEmpty()) {return BotMsgConstant.SEND_GPT_REQUEST_FAILURE;}
        GptMsg respMsg = JSONObject.parseObject(((JSONObject)choices.get(0)).getJSONObject("message").toJSONString(), GptMsg.class);
        if(respMsg == null) {return BotMsgConstant.SEND_GPT_REQUEST_FAILURE;}

        redisTemplate.opsForValue().set(cacheKey, respMsg, CommonConstant.GPT_MSG_TIMEOUT, TimeUnit.MINUTES);
        return gptModelType + ":" + respMsg.getContent();
    }
}
