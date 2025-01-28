package com.bot.takagi3.common.enumeration;

import com.mikuac.shiro.enums.ActionPath;
import lombok.Getter;

@Getter
public enum CustomActionPath implements ActionPath
{
    SEND_GROUP_NOTICE("_send_group_notice");

    private final String path;

    CustomActionPath(String path)
    {
        this.path = path;
    }

    @Override
    public String getPath()
    {
        return this.path;
    }
}
