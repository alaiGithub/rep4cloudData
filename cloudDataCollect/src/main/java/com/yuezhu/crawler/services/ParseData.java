package com.yuezhu.crawler.services;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;

import java.util.HashMap;
import java.util.Map;

public interface ParseData{
    //对请求的前置处理（具体是首页列表页面和链接对应的详情页面）
    void preProcess4Request(Page page);
    //获取网页中的数据
    void parseData4Page(Page page);
    //处理页面中的搜索条件
  default void dealwithSearchCondition(Page page){
      throw new UnsupportedOperationException();
  }
    default Map<String,String> getCookies(){
return null;
    }
    /**
     * @Description: 初始装载任务...
     * @Param:
     * @Return:
     * @Author: Mr.Chen
     * @Date: 2019/9/3 10:39
     */
    default Request[] getStartRequests() {
       throw new UnsupportedOperationException();
    }
    /**
    * @Description: 通过selenium 获取页面
            * @Param:
            * @Return:
            * @Author: Mr.Chen
            * @Date: 2019/9/3 21:01
            */
    default Html getHtmlBySelenium(Page page){
        throw new UnsupportedOperationException();
    }
}