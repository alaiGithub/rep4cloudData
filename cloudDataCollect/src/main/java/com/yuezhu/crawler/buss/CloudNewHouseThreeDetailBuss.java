package com.yuezhu.crawler.buss;
import com.yuezhu.crawler.services.ParseData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import javax.annotation.Resource;
/**
 * @program: crawler
 * @description: 房天下--新房--图片html(效果图)-url--采集
 * @author: Mr.Chen
 * @create: 2019-09-07 14:08
 **/
@Component
public class CloudNewHouseThreeDetailBuss implements PageProcessor {
    @Resource
    @Qualifier("fangNewHouseThreeDetailParse")
    private ParseData parseData;
    @Qualifier("fangNewHouseOneDetailPipeline")//留意下
    @Resource
    private Pipeline pipeline;
    private Site site;
    public CloudNewHouseThreeDetailBuss() {
    }
    @Override
    public void process(Page page) {
        parseData.preProcess4Request(page);
    }
    @Override
    public Site getSite() {
        site = Site.me().setRetryTimes(5).setSleepTime(1).setTimeOut(20 * 1000)// 400
                //.addHeader("Host", "sh.esf.fang.com")
//                .addCookie("id58","27514VyGHLD5ZBhTkyHR3g==")
//                .setCharset(cmConfig.getWebSiteCharset())//GBK UTF-8
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
    public void aggregateEachAndRun(){
        //1.采用代理时...
       /* HttpClientDownloader httpClientDownloader = HttpRequest.getMyHttpClientDownloader();
        Spider sd = MySpider.create(this).addPipeline(pipeline).thread(cmConfig.getThread());
        sd.setDownloader(httpClientDownloader);
        sd.run();*/
        //2.不是代理时...
        Spider spider=Spider.create(this).thread(1).addPipeline(pipeline);
        Request[] requestArr=this.parseData.getStartRequests();
        spider.addRequest(requestArr).run();
    }
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(CloudNewHouseThreeDetailBuss.class)
                .aggregateEachAndRun();
    }
}
