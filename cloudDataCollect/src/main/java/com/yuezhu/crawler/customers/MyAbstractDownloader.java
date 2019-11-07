package com.yuezhu.crawler.customers;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.selector.Html;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class MyAbstractDownloader implements MyDownloader {


    public Html download(String url) {
        return download(url, null,null);
    }


    public Html download(String url, String charset, AtomicInteger number) {
        Page page = download(new Request(url), Site.me().setCharset(charset).toTask(),number);
        return (Html) page.getHtml();
    }

    protected void onSuccess(Request request) {
    }

    protected void onError(Request request) {
    }

}
