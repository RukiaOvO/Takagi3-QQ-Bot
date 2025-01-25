package com.bot.takagi3.model;

import com.mikuac.shiro.common.utils.OneBotMedia;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class RandomAnimeImg
{
    private OneBotMedia img;

    private String title;

    private Boolean isR18;

    private String url;
}
