package com.yuezhu.crawler.model;

import lombok.Data;

@Data
public class CmConfig
{
    private String startUrl;
    private String tokens;
    private int thread = 1;
    private int sleepTime = 1;
    private String webSiteFlag;
    private String webSiteCharset;
    private String startUrlSuf;
}
