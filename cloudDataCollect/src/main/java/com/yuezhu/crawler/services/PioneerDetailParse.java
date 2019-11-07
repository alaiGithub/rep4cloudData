package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudPioneerParkMapper;
import com.yuezhu.crawler.model.CloudPioneerPark;
import com.yuezhu.util.ChromeDriverUtil;
import static com.yuezhu.util.ParseUtil.*;
import org.apache.commons.collections.CollectionUtils;
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

/**
 * @program: crawler
 * @description: 好租--创业园--详情--采集解析
 * @author: Mr.Chen
 * @create: 2019-09-18 10:25
 **/
@Component("pioneerDetailParse")
public class PioneerDetailParse implements ParseData{
    @Resource
    private CloudPioneerParkMapper cloudPioneerParkMapper;
    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }
    @Override
    public void parseData4Page(Page page) {
        page.setSkip(true);
        Selectable slTemp;
        String strTemp;
        List<Selectable> slsTemp;
        Html curHtml=this.getHtmlBySelenium(page);
        //主键.....
        page.putField("collectFlag","1");//已收集
        page.putField("id",page.getRequest().getHeaders().get("referer_id"));
        //name
        page.putField("pioneerName",curHtml.xpath("/html/body/div[2]/div[2]/div[1]/h1/span/text()").toString());
        //price_one
        page.putField("priceOne",curHtml.xpath("/html/body/div[2]/div[3]/div[2]/div[1]/span/span/text()").toString()+"元/月");
        //price_two
        page.putField("priceTwo",curHtml.xpath("/html/body/div[2]/div[3]/div[2]/div[1]/div/div/i[2]/span/text()").toString());
        //structure_acreage
        page.putField("structureAcreage",curHtml.xpath("/html/body/div[2]/div[3]/div[2]/div[2]/dl[1]/dt/i/span/text()").toString());
        //work num
        page.putField("containWorkNum",curHtml.xpath("/html/body/div[2]/div[3]/div[2]/div[2]/dl[2]/dt/i/span/text()").toString());
        //css ...about
        slTemp=curHtml.xpath("/html/body/style[@type='text/css']");
        System.out.println(slTemp);
        this.completeCssAbout(page,slTemp,curHtml);
        //overview...about
        slTemp=curHtml.xpath("/html/body/div[2]/div[4]/div[1]/ul/li");
        this.completeOverviewAbout(page,slTemp);
        //characteristic
        page.putField("houseCharacteristic",curHtml.xpath("/html/body/div[2]/div[4]/div[1]/div[1]/p/text()").all().toString());
        //pics
        mulFieldsToCombine(page,curHtml.xpath("/html/body/div[2]/div[4]/div[1]/div[2]/ul/li/img/@src"),"pics",";");
        //premises...
        slTemp=curHtml.xpath("/html/body/div[2]/div[4]/div[1]/div[3]/div[1]/ul/li");
        this.completePremisesAbout(page,slTemp);
        //matichings
        page.putField("surrondMatchings",curHtml.xpath("/html/body/div[2]/ul[1]/li/span/text()").all().toString());
    }
    private void completeCssAbout(Page page,Selectable selectable,Html html){
        String str;
        String cssStr=selectable.toString();
        //decoreate
        str=getContWithReg(cssStr,html.xpath("/html/body/div[2]/div[3]/div[2]/div[2]/dl[3]/dt/@class").toString()+"::before{content: \"","\";}");
        page.putField("decorateSituation",str);
        //address
        str=getContWithReg(cssStr,html.xpath("/html/body/div[2]/div[3]/div[2]/ul/li[1]/a[1]/@class").toString()+"::before{content: \"","\";}");//district
        str+="-"+getContWithReg(cssStr,html.xpath("/html/body/div[2]/div[3]/div[2]/ul/li[1]/a[2]/@class").toString()+"::before{content: \"","\";}");//road
        str+="-"+getContWithReg(cssStr,html.xpath("/html/body/div[2]/div[3]/div[2]/ul/li[1]/span/@class").toString()+"::before{content: \"","\";}");//road-num
        page.putField("address",str);
        //primises_name
        str=getContWithReg(cssStr,html.xpath("/html/body/div[2]/div[3]/div[2]/ul/li[3]/a/@class").toString()+"::before{content: \"","\";}");//primises_name
        page.putField("premisesName",str);
    }
    private  void completeOverviewAbout(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable eachSel =  selectables.get(i);
            if(eachSel.xpath("li/span[1]/text()").toString().contains("可注册")){
                page.putField("canRegister",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("免租时间")){
                page.putField("freeTime",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("最早可租")){
                page.putField("earliestRent",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("最短租期")){
                page.putField("shortestRent",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("装修情况")){
                page.putField("decorateInfos",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("价格优势")){
                page.putField("priceAdventage",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("看房时间")){
                page.putField("seeHouse",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
           else if(eachSel.xpath("li/span[1]/text()").toString().contains("面积信息")){
                page.putField("acreageInfos",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }

        }
    }
    private  void completePremisesAbout(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable eachSel =  selectables.get(i);
            if(eachSel.xpath("li/span[1]/text()").toString().contains("价格")){
                page.putField("premisesPrice",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
            else if(eachSel.xpath("li/span[1]/text()").toString().contains("地址")){
                page.putField("premisesAddress",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
            else if(eachSel.xpath("li/span[1]/text()").toString().contains("地铁")){
                page.putField("premiseSubway",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
            else if(eachSel.xpath("li/span[1]/text()").toString().contains("人气")){
                page.putField("premisePopularities",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }
            else if(eachSel.xpath("li/span[1]/text()").toString().contains("在租")){
                page.putField("premiseRenting",getTextFromHtml(eachSel.xpath("li/span[2]").toString()));
            }

        }
    }
    @Override
    public Request[] getStartRequests(){
        Request[] requests=null;
        CloudPioneerPark param=new CloudPioneerPark();
        param.setCollectFlag("0");//待处理任务
        param.setPageSize(1);//分页大小
        List<CloudPioneerPark> list=this.cloudPioneerParkMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudPioneerPark eachItem =  list.get(i);
                Request   request = new Request(eachItem.getWebUrl());
                request.addHeader("referer_id", eachItem.getId());
                requests[i]=request;
            }
        }
        return requests;
    }
    @Override
    public Html getHtmlBySelenium(Page page) {
      WebDriver driver= ChromeDriverUtil.getWebDriver();
        driver.get(page.getRequest().getUrl());
        WebElement webElement = driver.findElement(By.xpath("/html"));
        String str = webElement.getAttribute("outerHTML");
        Html html = new Html(str);
        return html;
    }

}

