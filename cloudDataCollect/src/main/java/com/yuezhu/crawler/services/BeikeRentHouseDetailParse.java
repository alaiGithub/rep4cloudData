package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudRentHouseMapper;
import com.yuezhu.crawler.model.CloudRentHouse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.yuezhu.util.ParseUtil.*;
import static com.yuezhu.util.StringUtil.*;
/**
 * @program: crawler
 * @description: 贝壳-租房-详情  数据采集业务处理
 * @author: Mr.Chen
 * @create: 2019-09-03 14:00
 **/
@Component("beikeRentHouseDetailParse")
public class BeikeRentHouseDetailParse implements ParseData {
    @Resource
    private CloudRentHouseMapper cloudRentHouseMapper;
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
        strTemp=curHtml.xpath("/html/body/div[3]/div[1]/div[3]/p/text()").toString();//当做标示用（是否加载成功）
        //附加加载太快或其他原因导致没有加载完成的原因 需要验证码...begin
        if(StringUtils.isBlank(strTemp) ||"null".equals(strTemp)){
            page.setSkip(true);
            return;
        }
        //附加加载太快或其他原因导致没有加载完成的原因 需要验证码...end
        //name
        page.putField("name",strTemp);
        //price
        strTemp=curHtml.xpath("//*[@id=\"aside\"]/p[1]/span/text()").toString();
        strTemp+=repBlk(curHtml.xpath("//*[@id=\"aside\"]/p[1]/text()").toString());
        page.putField("rentPriceOne",strTemp);
        //labels...
        slTemp=curHtml.xpath("//*[@id=\"aside\"]/p[2]/i/text()");
        page.putField("labels",slTemp.all().toString());//备用
        //lebels...about
        slTemp=curHtml.xpath("//*[@id=\"aside\"]/p[2]/i");
        this.completeLabelsAbout(page,slTemp);
        //<!-- 房源户型、朝向、面积、租赁方式 -->
        slTemp=curHtml.xpath("//*[@id=\"aside\"]/ul[1]/p/span");
        this.completeHouseBasicAbout(page,slTemp);
        //publish...
        page.putField("publishManName",curHtml.xpath("//*[@id=\"aside\"]/ul[2]/li/div[1]/span/@title").toString());
        page.putField("publishManPhone",curHtml.xpath("//*[@id=\"phone1\"]/text()").toString());
       //basic detail info
        slTemp=curHtml.xpath("/html/body/div[3]/div[1]/div[3]/div[2]/div[2]/ul/li/text()");
        this.completeHouseBasicInfo(page,slTemp);
        //matchings
        mulFieldsToCombine(page,"matchings","/html/body/div[3]/div[1]/div[3]/div[2]/ul/li[@class='fl oneline  ']/text()",";");
       //desc
        mulFieldsToCombine(page,"backup3","//*[@id=\"desc\"]/ul/li/p[1]/text()",";");//留意下
       //pics
        slTemp=curHtml.xpath("//*[@id=\"prefix\"]/li/img/@src");
       this.completePics(page,slTemp);
    }
    @Override
    public Request[] getStartRequests() {
        Request[] requests=null;
        CloudRentHouse param=new CloudRentHouse();
        param.setCollectFlag("0");//待处理任务
        param.setPageSize(3000);//分页大小
        List<CloudRentHouse> list=this.cloudRentHouseMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudRentHouse eachItem =  list.get(i);
             Request   request = new Request(eachItem.getWebUrl());
                request.addHeader("referer_id", eachItem.getId().toString());
                requests[i]=request;
            }
        }
        return requests;
    }

    private void completeLabelsAbout(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                Selectable s =  selectables.get(i);
                if("authorization_apartment".equals(s.xpath("i/@data-class").toString())){
                    page.putField("houseCatagory",s.xpath("i/text()").toString());
                }
               else if("rent_period_month".equals(s.xpath("i/@data-class").toString())){
                    page.putField("rentTimeMethod",s.xpath("i/text()").toString());
                }
               else if("is_subway_house".equals(s.xpath("i/@data-class").toString())){
                    page.putField("backup1",s.xpath("i/text()").toString());//留意下
                }
               else if("decoration".equals(s.xpath("i/@data-class").toString())){
                    page.putField("decorationSituation",s.xpath("i/text()").toString());
                }
               else if("deposit_1_pay_1".equals(s.xpath("i/@data-class").toString())){
                    page.putField("payMethod",s.xpath("i/text()").toString());
                }
               else if("is_new".equals(s.xpath("i/@data-class").toString())){
                    page.putField("backup2",s.xpath("i/text()").toString());
                }
               //...
            }
        }
    }
    private void completeHouseBasicAbout(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                Selectable s =  selectables.get(i);
                if("house".equals(s.xpath("span/i/@class").toString())){
                    page.putField("wholePart",s.xpath("span/text()").toString());
                }
               else if("typ".equals(s.xpath("span/i/@class").toString())){
                    page.putField("houseType",s.xpath("span/text()").toString());
                }
               else if("area".equals(s.xpath("span/i/@class").toString())){
                    page.putField("houseAcreage",s.xpath("span/text()").toString());
                }
               else if("orient".equals(s.xpath("span/i/@class").toString())){
                    page.putField("houseOrient",s.xpath("span/text()").toString());
                }
            }
        }
    }
    private void completeHouseBasicInfo(Page page,Selectable selectable){
        List<String> selectables=selectable.all();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                String s =  selectables.get(i);
                if(s.contains("发布：")){
                    page.putField("publishTime",s.replace("发布：",""));
                }
                else if(s.contains("入住：")){
                    page.putField("enterRentTime",s.replace("入住：",""));
                }
                else if(s.contains("租期：")){
                    page.putField("rentTime",s.replace("租期：",""));
                }
                else if(s.contains("看房：")){
                    page.putField("seeHouseTime",s.replace("看房：",""));
                }
                else if(s.contains("楼层：")){
                    page.putField("floorLocation",s.replace("楼层：",""));
                }
                else if(s.contains("电梯：")){
                    page.putField("elevator",s.replace("电梯：",""));
                }
                else if(s.contains("车位：")){
                    page.putField("parkingSpace",s.replace("车位：",""));
                }
                else if(s.contains("用水：")){
                    page.putField("useWater",s.replace("用水：",""));
                }
                else if(s.contains("用电：")){
                    page.putField("usePower",s.replace("用电：",""));
                }
                else if(s.contains("燃气：")){
                    page.putField("useGas",s.replace("燃气：",""));
                }
            }
        }
    }
    private void completePics(Page page,Selectable selectable){
        StringBuffer sb=new StringBuffer();
        List<String> selectables=selectable.all();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                String s =  selectables.get(i).replace("126x86","780x439")+";";//留意下
                sb.append(s);
            }
        }
        page.putField("pics",sb.toString());
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
}
