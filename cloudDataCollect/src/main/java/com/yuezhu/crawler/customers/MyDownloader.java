package com.yuezhu.crawler.customers;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.concurrent.atomic.AtomicInteger;

public interface MyDownloader {

    public Page download(Request request, Task task, AtomicInteger number);

    public void setThread(int threadNum);
}
