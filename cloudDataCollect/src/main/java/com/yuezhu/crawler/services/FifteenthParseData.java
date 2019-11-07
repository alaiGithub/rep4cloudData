package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudDataShopMapper;
import com.yuezhu.crawler.model.CloudDataShop;
import com.yuezhu.util.ParseUtil;
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

/**
 * @program: crawler
 * @description: 商铺   租房/售房    --详情解析
 * @author: Mr.Chen
 * @create: 2019-08-30 16:02
 **/
@Component("fifteenthParseData")
public class FifteenthParseData implements ParseData {
    @Resource
    private CloudDataShopMapper cloudDataShopMapper;

    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }

    @Override
    public void parseData4Page(Page page) {
        Selectable slTemp;
        String strTemp;
        List<Selectable> slsTemp;
        Html curHtml = page.getHtml();
        //主键.....
        page.putField("id", page.getRequest().getHeaders().get("referer_id"));
        page.putField("collectFlag", "1");//已收集
        //name
        slTemp = curHtml.xpath("/html/body/div[4]/div[1]/h1/text()");
        page.putField("name", slTemp.toString().trim());
        //unit_price unit
        slTemp = curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p[1]/span[@class='house_basic_title_money_num_chuzu']/text()");
        strTemp = slTemp.toString();
        slTemp = curHtml.xpath("/html/body/div[4]/div[2]/div[2]/p[1]/span[@class='house_basic_title_money_unit_chuzu']/text()");
        strTemp += slTemp.toString();
        page.putField("rentPriceTwo", strTemp);
        //area
        slTemp = curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[1]/text()");
        strTemp = slTemp.toString().replaceAll("&nbsp;|\\s", "");
        page.putField("areaLacation", strTemp);
        //address
        slTemp = curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[2]/text()");
        strTemp = slTemp.toString().replaceAll("&nbsp;|\\s", "");
        page.putField("address", strTemp);
        //publish_man
        slTemp = curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[2]/*[@class='poster-name']/text()");
        strTemp = slTemp.toString().replaceAll("&nbsp;|\\s", "");
        page.putField("publishMan", strTemp);
        //publish_man_phone
        slTemp = curHtml.xpath("//*[@id=\"houseChatEntry\"]/div/p[@class='phone-num']/text()");
        strTemp = slTemp.toString().replaceAll("&nbsp;|\\s", "");
        page.putField("publishManPhone", strTemp);
        //publish_man_role 个人或经济人
        slTemp = curHtml.xpath("/html/body/div[4]/div[2]/div[2]/div[2]/*[@class='poster-identity']/text()");
        if (slTemp.toString() != null) {
            strTemp = slTemp.toString().replaceAll("&nbsp;|\\s", "");
            page.putField("publishSource", strTemp);//个人
        } else {
            page.putField("publishSource", "经济人");
        }

        //overview...
        this.complete4overview(page, curHtml.xpath("//*[@id=\"intro\"]/ul/li"));
        //desc
        slTemp = curHtml.xpath("//*[@id=\"generalSound\"]/div/text()");
        strTemp = slTemp.toString().replaceAll("&nbsp;|\\s", "");
        page.putField("detailDesc", strTemp);
        //matching  matching
        ParseUtil.mulFieldsToCombine(page, "matching", "//*[@id=\"peitao\"]/div/ul/li[@class='peitao-on']/text()", ";");
        //pics
        ParseUtil.mulFieldsToCombine(page, "pics", "//*[@id=\"generalType\"]/div/ul/li/img/@src", ";");

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
            //month rent
            if ("月租".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("rentPriceOne", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //rent method
            else if ("押付".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("rentMethod", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //begin time
            else if ("起租期".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("rentBeginDate", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //structure acreage
            else if ("建筑面积".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("structureAcreage", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //shop property
            else if ("商铺性质".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("shopProperty", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //shop type
            else if ("商铺类型".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("catagory", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //manager stituation
            else if ("经营状态".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("managerSituation", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //manager type
            else if ("经营类型".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("managerType", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //floor_situation
            else if ("楼层".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("floorSituation", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //specification
            else if ("规格".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("specification", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //passenger_flow
            else if ("客流人群".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("passengerFlow", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //aboutFee
            else if ("相关费用".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("aboutFee", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //sale_price_total
            else if ("总价".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("salePriceTotal", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //sale_price_unit
            else if ("单价".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("salePriceUnit", eachSel.xpath("span[@class='content']/text()").toString());
            }
            //expect_income
            else if ("预期收益".equals(eachSel.xpath("span[@class='title']/text()").toString())) {
                page.putField("expectIncome", eachSel.xpath("span[@class='content']/text()").toString());
            }

        }
    }

    @Override
    public Request[] getStartRequests() {
        Request[] requests = null;
        //从数据库中读取待处理任务
        CloudDataShop param = new CloudDataShop();
        param.setCollectFlag("0");//待处理任务
        param.setPageSize(3000);//分页大小
        List<CloudDataShop> list = this.cloudDataShopMapper.selectByObj(param);
        //构建request对象==》task
        if (CollectionUtils.isNotEmpty(list)) {
            requests = new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudDataShop cloudDataShop = list.get(i);
                Request request = new Request(cloudDataShop.getWebUrl());
                request.addHeader("referer_id", cloudDataShop.getId().toString());
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
