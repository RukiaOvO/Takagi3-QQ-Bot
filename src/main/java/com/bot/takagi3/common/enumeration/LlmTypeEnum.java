package com.bot.takagi3.common.enumeration;

public enum LlmTypeEnum
{
    DOU_BAO("Doubao"),
    GPT_4O_MINI("Gpt-4o-mini"),
    DEEP_SEEK("Deepseek");
    private final String llmType;

    LlmTypeEnum(String llmType)
    {
        this.llmType = llmType;
    }

    public String getPath()
    {
        return this.llmType;
    }
}
