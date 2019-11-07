package com.yuezhu.crawler.services;

import com.yuezhu.crawler.dao.CloudParkingSpaceMapper;
import com.yuezhu.crawler.model.CloudParkingSpace;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yuezhu.util.ParseUtil.*;

/**
 * @program: crawler
 * @description: 58--停车位采集--解析
 * @author: Mr.Chen
 * @create: 2019-09-17 09:27
 **/
@Component("parkingDetailParse")
public class ParkingDetailParse implements ParseData{
    @Resource
    private CloudParkingSpaceMapper cloudParkingSpaceMapper;
    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }
    @Override
    public void parseData4Page(Page page) {
        Selectable slTemp;
        String strTemp;
        List<Selectable> slsTemp;
        Html curHtml=this.getHtmlBySelenium(page);
        //主键.....
        page.putField("collectFlag","1");//已收集
        page.putField("id",page.getRequest().getHeaders().get("referer_id"));
        //name
        page.putField("parkingName",curHtml.xpath("/html/body/div[4]/div[1]/h1/text()").toString());
       //publish_time
        page.putField("publishTime",curHtml.xpath("/html/body/div[4]/div[1]/p/span[1]/text()").toString());
        //price_one
        strTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_num']/text()").toString();
        strTemp+=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_unit']/text()").toString();
        page.putField("priceOne",strTemp);
        //price_two
        page.putField("priceTwo",curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_num_chushou']/text()").toString());
        //basic info ...
        slTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/ul/li/span");
        this.completeBasicInfo(page,slTemp);
        //address
        strTemp="";
        List<Selectable> list=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/ul/li").nodes();
        Selectable selectable4Address=list.get(list.size()-1);//取最后一个li
        List<String>  linkTexts= selectable4Address.xpath("a/text()").all();
        for (int i = 0; i < linkTexts.size(); i++) {
            String s =  linkTexts.get(i);
            strTemp+=s+"-";
        }
        strTemp+=repBlk(selectable4Address.xpath("span[contains(@class,'xxdz-des')]/text()").toString());
        page.putField("parkingAddress",strTemp);
        //publish_man
        page.putField("publishManName",curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[@class='poster-name']/text()").toString());
        //publish_phone
        page.putField("publishManPhone",curHtml.xpath("//*[@id=\"houseChatEntry\"]/div/p[@class='phone-num']/text()").toString());
       //desc
        page.putField("parkingDesc",curHtml.xpath("//*[@id=\"generalSound\"]/div//text()").toString());
        //pics
       mulFieldsToCombine(page,"pics","//*[@id=\"generalType\"]/div[1]/ul/li/img/@src",";");
    }
    private void completeBasicInfo(Page page,Selectable selectable){
        List<Selectable> nodes=selectable.nodes();
        for (int i = 0; i < nodes.size(); i++) {
            Selectable each =  nodes.get(i);
            Selectable nextEach;
            if(i==nodes.size()-1){
                nextEach=each;
            }
            else{
                nextEach =  nodes.get(i+1);//留意下
            }
            if("类型:".equals(repBlk(each.xpath("span/text()").toString()))){
                page.putField("parkingCatagory",getTextFromHtml(nextEach.xpath("span").toString()));
            }
           else if("面积:".equals(repBlk(each.xpath("span/text()").toString()))){
                page.putField("parkingAcreage",getTextFromHtml(nextEach.xpath("span").toString()));
            }
           else if("首付:".equals(repBlk(each.xpath("span/text()").toString()))){
                page.putField("firstPay",getTextFromHtml(nextEach.xpath("span").toString()));
            }
           else if("车位类型:".equals(repBlk(each.xpath("span/text()").toString()))){
                page.putField("parkingType",getTextFromHtml(nextEach.xpath("span").toString()));
            }
        }
    }
    @Override
    public Request[] getStartRequests(){
        Request[] requests=null;
        CloudParkingSpace param=new CloudParkingSpace();
        param.setCollectFlag("0");//待处理任务
        param.setPageSize(3000);//分页大小
        List<CloudParkingSpace> list=this.cloudParkingSpaceMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudParkingSpace eachItem =  list.get(i);
                Request   request = new Request(eachItem.getWebUrl());
                request.addHeader("referer_id", eachItem.getId());
                requests[i]=request;
            }
        }
        return requests;
    }
    @Override
    public Html getHtmlBySelenium(Page page) {
      /*  ChromeOptions chromeOptions=new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);*/
      /*  driver.get(page.getRequest().getUrl());
        WebElement webElement = driver.findElement(By.xpath("/html"));
        String str = webElement.getAttribute("outerHTML");
        System.out.println(str);
        Html html = new Html(str);*/
        return page.getHtml();
    }

    @Override
    public Map<String, String> getCookies() {
        Map<String,String> map=new HashMap<>();
        map.put("id58","xxx");
        return map;
    }
}
