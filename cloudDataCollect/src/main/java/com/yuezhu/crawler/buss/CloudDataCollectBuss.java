package com.yuezhu.crawler.buss;
import com.yuezhu.crawler.model.CmConfig;
import com.yuezhu.crawler.pipeline.CloudDataCollectPipeline;
import com.yuezhu.crawler.services.CreateParseData;
import com.yuezhu.crawler.services.ParseData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import javax.annotation.Resource;
@Component
public class CloudDataCollectBuss implements PageProcessor {
    @Qualifier("cloudDataCollectPipeline")
    @Resource
    private CloudDataCollectPipeline cloudDataCollectPipeline;
    @Resource
    private CmConfig cmConfig;
    private  Site site;
    private ParseData parseData;

    public CloudDataCollectBuss() {
    }

    /*
        date:2019/04/11
        autor:chenHongLai
        desc:核心业务处理方法==》对请求预处理
        */
    @Override
    public void process(Page page) {
        int inputInt=Integer.valueOf(cmConfig.getWebSiteFlag());
        CreateParseData createParseData=new CreateParseData();
        parseData= createParseData.getParseDataByFlag(inputInt);
        parseData.preProcess4Request(page);
    }
    @Override
    public Site getSite() {
        site = Site.me().setRetryTimes(5).setSleepTime(cmConfig.getSleepTime()).setTimeOut(20 * 1000)// 400
                //.addHeader("Host", "sh.esf.fang.com")
//                .addCookie("id58","27514VyGHLD5ZBhTkyHR3g==")
                .setCharset(cmConfig.getWebSiteCharset())
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        //.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
//        //循环加入cookies
//        Set<Map.Entry<String,String>>   entries=parseData.getCookies().entrySet();
//        Iterator<Map.Entry<String,String>> iterator=entries.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String,String> entry=iterator.next();
//            site.addCookie(entry.getKey(),entry.getValue());
//        }
        return site;
    }
    /*
    date:2019/04/11/
    autor:chenHongLai
    desc:将每一个组件组合在一起
    */
    public void aggregateEachAndRun(){
        Spider.create(this)
                .addUrl(cmConfig.getStartUrl())//初始导航   动态改变
                .addPipeline(cloudDataCollectPipeline)
                .thread(cmConfig.getThread())
                .run();
    }
}
