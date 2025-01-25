package com.bot.takagi3.util;

import com.bot.takagi3.boot.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil
{
    public static final String IMG_DATA_DIR = "data/img/";

    public static final String LOCAL_FILE_PREFIX = "file://";

    public static String getFormattedLocalTime()
    {
        DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localTimeFormatter.format(LocalTime.now());
    }

    public static String getFormattedDateTime()
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
        return dateTimeFormatter.format(LocalDate.now());
    }

    public static String getFormattedLocalDateTime()
    {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
        return timeFormatter.format(LocalDateTime.now());
    }

    public static String getImgStaticUrlByName(String imgName)
    {
        return LOCAL_FILE_PREFIX + app.class.getClassLoader().getResource("").getPath() + IMG_DATA_DIR + imgName;
    }
}
