package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudDataOfficeMapper;
import com.yuezhu.crawler.model.CloudDataOffice;
import static com.yuezhu.util.ParseUtil.*;
import static com.yuezhu.util.StringUtil.*;

import com.yuezhu.crawler.model.CloudDataShop;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: crawler
 * @description: 58--写字楼(租，售)
 * @author: Mr.Chen
 * @create: 2019-09-02 09:48
 **/
@Transactional
@Component("seventeenthParseData")
public class SeventeenthParseData implements ParseData{
    @Resource
    private CloudDataOfficeMapper cloudDataOfficeMapper;
    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }
    @Override
    public void parseData4Page(Page page) {
        Selectable slTemp;
        String strTemp;
        List<Selectable> slsTemp;
        Html curHtml=page.getHtml();
        //主键.....
        page.putField("id",page.getRequest().getHeaders().get("referer_id"));
        page.putField("collectFlag","1");//已收集
        strTemp=curHtml.xpath("/html/body/div[4]/div[@class='house-title']/h1/text()").toString();//当做标示用（是否加载成功）
        //附加加载太快或其他原因导致没有加载完成的原因 需要验证码...begin
        if(StringUtils.isBlank(strTemp) ||"null".equals(strTemp)){
            page.setSkip(true);
            return;
        }
        //附加加载太快或其他原因导致没有加载完成的原因 需要验证码...end
        //name
        page.putField("name",strTemp);
       // rent_price ...
        strTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_num']/text()").toString();
        strTemp+=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_unit']/text()").toString();
        page.putField("rentPriceTwo",repBlk(strTemp));
        page.putField("salePriceOne",repBlk(strTemp));

        strTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class=' house_basic_title_money_num_chuzu']/text()").toString();
        strTemp+=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_unit_chuzu']/text()").toString();
        page.putField("rentPriceOne",strTemp);

        strTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p/span[@class='house_basic_title_money_num_chushou']/text()").toString();
        page.putField("salePriceTwo",strTemp);
       //catagory
        page.putField("catagory",curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[3]/span[1]/a/text()").toString());
       //split
        page.putField("isSplit",curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[2]/span[2]/text()").toString());
        //level
        page.putField("level",curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[3]/span[2]/text()").toString());
        //office_premises
        page.putField("officePremises",repBlk(curHtml.xpath("/html/body/div[4]/div[2]/div[2]/ul/li[1]/span[2]/span/text()").toString()));
        //detail_address
        strTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/a[1]/text()").toString();
        strTemp+="-"+curHtml.xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/a[2]/text()").toString();
        strTemp+=" "+repBlk(curHtml.xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/span/text()").toString());
        page.putField("address",strTemp);
        //publish...
        page.putField("publishManName",curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[2]/*[@class='poster-name']/text()").toString());
        page.putField("publishManPhone",curHtml.xpath("//*[@id=\"houseChatEntry\"]/div/p[@class='phone-num']/text()").toString());
        slTemp=curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[2]/p[@class='poster-identity']/text()");
        if(slTemp.toString()!=null){
            strTemp=repBlk(slTemp.toString());
            page.putField("publishSource",strTemp);//个人
        }
        else{
            page.putField("publishSource","经济人");
        }
       //overview...
        this.complete4overview(page,curHtml.xpath("//*[@id=\"generalSituation\"]/div/ul/li"));
        //desc
        page.putField("desc",filterOffUtf8Mb4(getTextFromHtml(curHtml.xpath("//*[@id=\"generalSound\"]/div").toString())));//
        //matching  matching
        mulFieldsToCombine(page,"matchings","//*[@id=\"peitao\"]/div/ul/li[@class='peitao-on']/text()",";");
        //pics
        mulFieldsToCombine(page,"pics","//*[@id=\"generalType\"]/div/ul/li/img/@src",";");

    }
    /**
     * @Description: 概况相关信息
     * @Param:
     * @Return:
     * @Author: Mr.Chen
     * @Date: 2019/8/30 20:10
     */
    private void complete4overview(Page page, Selectable selectable) {
        List<Selectable> selectables = selectable.nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable eachSel = selectables.get(i);
            //location_floor
            if ("所在楼层".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("locationFloor", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //structure_acreage
            else if ("建筑面积".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("structureAcreage", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //decoration_situaion
            else if ("装修情况".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("decorationSituaion", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //can_register
            else if ("可注册公司".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("canRegister", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //user_rate
            else if ("使用率".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("userRate", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //begin_rent_date
            else if ("起租期：".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("beginRentDate", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //pay_method
            else if ("付款方式：".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("payMethod", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
            //property fee
            else if ("物业费：".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("propertyFee", getTextFromHtml(eachSel.xpath("span[@class='c_000']").toString()));
            }
            //refer_station_num
            else if ("参考容纳工位数：".equals(eachSel.xpath("span[@class='mr_25 c_999']/text()").toString())) {
                page.putField("referStationNum", eachSel.xpath("span[@class='c_000']/text()").toString());
            }
        }
    }
    @Override
    public Request[] getStartRequests() {
        Request[] requests = null;
        CloudDataOffice param=new CloudDataOffice();
        param.setCollectFlag("0");//待处理任务
        param.setRentSaleFlag("1");
        param.setPageSize(100);//分页大小
        List<CloudDataOffice> list=this.cloudDataOfficeMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests = new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudDataOffice eachItem =  list.get(i);
                Request   request = new Request(eachItem.getWebUrl());
                request.addHeader("referer_id", eachItem.getId().toString());
                requests[i] = request;
            }
        }
        return requests;
    }

    @Override
    public Map<String, String> getCookies() {
        Map<String,String> map=new HashMap<>();
        map.put("id58","xxx");
        return map;
    }
}
