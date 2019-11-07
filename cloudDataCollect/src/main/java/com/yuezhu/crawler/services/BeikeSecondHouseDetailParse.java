package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudSecondHouseMapper;
import com.yuezhu.crawler.model.CloudSecondHouse;
import com.yuezhu.util.BrowserDriverUtil;
import com.yuezhu.util.StringUtil;
import com.yuezhu.util.TestPhantomJsDriver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.List;
import static com.yuezhu.util.ParseUtil.*;
import static com.yuezhu.util.ParseUtil.driver;

/**
 * @program: crawler
 * @description: 贝壳--二手房--详情--采集解析
 * @author: Mr.Chen
 * @create: 2019-09-05 09:20
 **/
@Component("beikeSecondHouseDetailParse")
public class BeikeSecondHouseDetailParse implements ParseData {
    @Resource
    private CloudSecondHouseMapper cloudSecondHouseMapper;
    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }
    @Override
    public void parseData4Page(Page page) {
        Selectable slTemp;
        String strTemp;
        List<Selectable> slTemps;
//        Html curHtml=page.getHtml();
        Html curHtml=this.getHtmlBySelenium(page);
        //主键.....
        page.putField("collectFlag","1");//已收集
        page.putField("id",page.getRequest().getHeaders().get("referer_id"));
        strTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[2]/div[2]/div/div/div[1]/h1/@title").toString();//当做标示用（是否加载成功）
        //附加加载太快或其他原因导致没有加载完成的原因 需要验证码...begin
        if(StringUtils.isBlank(strTemp) ||"null".equals(strTemp)){
            page.setSkip(true);
            return;
        }
       //name
        page.putField("name",strTemp);
        //total price
        strTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[1]/span[1]/text()").toString();
        strTemp+=curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[1]/span[2]/span/text()").toString();
        page.putField("backup1",strTemp);
        //unit price
        strTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[1]/div[1]/div[1]/span/text()").toString();
        strTemp+=curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[1]/div[1]/div[1]/i/text()").toString();
        page.putField("backup2",strTemp);
        //community name
        page.putField("communityName",curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[3]/div[1]/a[1]/@title").toString());
        //area_location
        strTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[3]/div[2]/span[2]/a[1]/text()").toString();
        strTemp+=" "+curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[3]/div[2]/span[2]/a[2]/text()").toString();
        page.putField("areaLocation",strTemp);
        //see_house_time
        page.putField("seeHouseTime",curHtml.xpath("//*[@id=\"beike\"]/div/div[4]/div/div[2]/div[3]/div[3]/span[2]/text()").toString());
        //publish...
        page.putField("publishManName",curHtml.xpath("//*[@id=\"zuanzhan\"]/div[2]/div/div[1]/div[2]/div[1]/a[1]/@title").toString());
       strTemp=curHtml.xpath("//*[@id=\"zuanzhan\"]/div[2]/div/div[2]/div[2]/text()").toString();
       if(strTemp!=null&&strTemp.length()==14){
           strTemp=strTemp.substring(0,10)+"转"+strTemp.substring(10);
       }
        page.putField("publishManPhone",strTemp);
        //basic info  trade info ....
        slTemp=curHtml.xpath("//*[@id=\"introduction\"]/div/div/div/div[2]/ul/li");
        this.completeBasicAndTradInfo(page,slTemp);
        //this house discrimate distinguishing
        slTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[5]/div[1]/div[2]/div/div");
        this.completeHouseCharacters(page,slTemp);
        //pic ....
        mulFieldsToCombine(page,"pics","//*[@id=\"thumbnail2\"]/ul/li/@data-src",";");
        //city
        strTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[2]/div[3]/div/div/a[2]/text()").toString();
        if(StringUtils.isNotBlank(strTemp)){
            page.putField("premisesCity",strTemp.substring(0,strTemp.indexOf("二手房")));
        }
        //district
        strTemp=curHtml.xpath("//*[@id=\"beike\"]/div/div[2]/div[3]/div/div/a[3]/text()").toString();
        if(StringUtils.isNotBlank(strTemp)){
            page.putField("premisesDistrict",strTemp.substring(0,strTemp.indexOf("二手房")));
        }
    }
    @Override
    public Request[] getStartRequests() {
        Request[] requests=null;
        CloudSecondHouse param=new CloudSecondHouse();
        param.setCollectFlag("0");//待处理任务
        param.setPageSize(3000);//分页大小
        List<CloudSecondHouse> list=this.cloudSecondHouseMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudSecondHouse eachItem =  list.get(i);
                Request   request = new Request(eachItem.getWebUrl());
                request.addHeader("referer_id", eachItem.getId().toString());
                requests[i]=request;
            }
        }
        return requests;
    }
    @Override
    public Html getHtmlBySelenium(Page page) {
        Html html;
        //1.使用selenium
////         WebDriver driver= TestPhantomJsDriver.getPhantomJSDriver();
//         WebDriver driver= BrowserDriverUtil.getWebDriver();
//         driver.get(page.getRequest().getUrl());
//        WebElement webElement = driver.findElement(By.cssSelector("html"));
//        String str = webElement.getAttribute("outerHTML");
//        html = new Html(str);
        //2.不使用selenium
      html = page.getHtml();
        return html;
    }
private void completeBasicAndTradInfo(Page page,Selectable selectable){
    List<Selectable> selectables=selectable.nodes();
    if(CollectionUtils.isNotEmpty(selectables)){
        for (int i = 0; i < selectables.size(); i++) {
            Selectable s =  selectables.get(i);
            if("房屋户型".equals(s.xpath("li/span/text()").toString())){
                page.putField("houseType",s.xpath("li/text()").toString());
            }
           else if("所在楼层".equals(s.xpath("li/span/text()").toString())){
                page.putField("floorLocation",s.xpath("li/text()").toString());
            }
           else if("建筑面积".equals(s.xpath("li/span/text()").toString())){
                page.putField("structureAcreage",s.xpath("li/text()").toString());
            }
           else if("户型结构".equals(s.xpath("li/span/text()").toString())){
                page.putField("houseConstruction",s.xpath("li/text()").toString());
            }
           else if("建筑类型".equals(s.xpath("li/span/text()").toString())){
                page.putField("structureType",s.xpath("li/text()").toString());
            }
           else if("房屋朝向".equals(s.xpath("li/span/text()").toString())){
                page.putField("houseOrient",s.xpath("li/text()").toString());
            }
           else if("建筑结构".equals(s.xpath("li/span/text()").toString())){
                page.putField("buildConstruction",s.xpath("li/text()").toString());
            }
           else if("装修情况".equals(s.xpath("li/span/text()").toString())){
                page.putField("decorationCondition",s.xpath("li/text()").toString());
            }
           else if("梯户比例".equals(s.xpath("li/span/text()").toString())){
                page.putField("terraceRate",s.xpath("li/text()").toString());
            }
           else if("配备电梯".equals(s.xpath("li/span/text()").toString())){
                page.putField("hasElevator",s.xpath("li/text()").toString());
            }
           else if("产权年限".equals(s.xpath("li/span/text()").toString())){
                page.putField("propertyAge",s.xpath("li/text()").toString());
            }
           else if("挂牌时间".equals(s.xpath("li/span/text()").toString())){
                page.putField("listTime",repBlk(s.xpath("li/text()").toString()));
            }
           else if("交易权属".equals(s.xpath("li/span/text()").toString())){
                page.putField("tradeRight",repBlk(s.xpath("li/text()").toString()));
            }
           else if("上次交易".equals(s.xpath("li/span/text()").toString())){
                page.putField("lastTrade",repBlk(s.xpath("li/text()").toString()));
            }
           else if("房屋用途".equals(s.xpath("li/span/text()").toString())){
                page.putField("houseUsing",repBlk(s.xpath("li/text()").toString()));
            }
           else if("房屋年限".equals(s.xpath("li/span/text()").toString())){
                page.putField("houseAge",repBlk(s.xpath("li/text()").toString()));
            }
           else if("产权所属".equals(s.xpath("li/span/text()").toString())){
                page.putField("propertyOwning",repBlk(s.xpath("li/text()").toString()));
            }
           else if("抵押信息".equals(s.xpath("li/span/text()").toString())){
                page.putField("mortgage",repBlk(s.xpath("li/span/text()").all().get(1)));
            }
           else if("房本备件".equals(s.xpath("li/span/text()").toString())){
                page.putField("roomBookRecord",repBlk(s.xpath("li/text()").toString()));
            }
        }
    }
}
private void completeHouseCharacters(Page page,Selectable selectable){
    List<Selectable> selectables=selectable.nodes();
    if(CollectionUtils.isNotEmpty(selectables)){
        for (int i = 0; i < selectables.size(); i++) {
            Selectable s =  selectables.get(i);
            if("房源特色".equals(s.xpath("div[@class='name']/text()").toString())){
                page.putField("houseCharacters",s.xpath("div[@class='content']/a/text()").all().toString());
            }
           else if("核心卖点".equals(s.xpath("div[@class='name']/text()").toString())){
                page.putField("saleFeature",repBlk(s.xpath("div[@class='content']/text()").toString()));
            }
           else if("小区介绍".equals(s.xpath("div[@class='name']/text()").toString())){
                page.putField("communityIntroduce",repBlk(s.xpath("div[@class='content']/text()").toString()));
            }
           else if("周边配套".equals(s.xpath("div[@class='name']/text()").toString())){
                page.putField("surrondMatchings",repBlk(s.xpath("div[@class='content']/text()").toString()));
            }
           else if("交通出行".equals(s.xpath("div[@class='name']/text()").toString())){
                page.putField("trafficTravel",repBlk(s.xpath("div[@class='content']/text()").toString()));
            }
        }
    }
}
}
