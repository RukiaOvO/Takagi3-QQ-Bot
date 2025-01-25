package com.bot.takagi3.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bot.takagi3.constant.RequestParamConstant;
import com.bot.takagi3.model.RandomAnimeImg;
import com.bot.takagi3.service.HttpRequestService;
import com.bot.takagi3.util.HttpUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HttpRequestServiceImpl implements HttpRequestService
{

    @Override
    public RandomAnimeImg postForRandomAnimeImg(String tag, int isR18)
    {
        String replyMsg;

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
}
