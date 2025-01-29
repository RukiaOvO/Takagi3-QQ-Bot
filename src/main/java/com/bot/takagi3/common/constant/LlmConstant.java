package com.bot.takagi3.common.constant;

public class LlmConstant
{
    private LlmConstant(){}

    public static final String LLM_SYSTEM = "system";

    public static final String LLM_USER = "user";

    public static final String LLM_MEM_RELOAD_ORDER = "重置";

    public static final String LLM_MEM_RELOAD_RESP = "重置记忆完成";

    public static final String LLM_INIT_MSG = "你是“大唐盛世BigTBot”，是由唐氏儿开发唐氏儿第二代智能唐氏儿";

    public static final String DOU_BAO_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

    public static final String GPT_URL = "https://api.chatanywhere.tech/v1/chat/completions";

    public static final String DEEP_SEEK_URL = "https://api.deepseek.com/v3/chat/completions";

    public static final Integer LLM_REQUEST_TIMEOUT = 20;

    public static final String LLM_MSG_CACHE_PREFIX = "LlmMsg-";
}
