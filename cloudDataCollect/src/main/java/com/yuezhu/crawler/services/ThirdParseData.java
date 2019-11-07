package com.yuezhu.crawler.services;

import com.yuezhu.crawler.enums.Website;
import com.yuezhu.crawler.model.CloudDataCollect;
import com.yuezhu.util.ParseUtil;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
@Component("thirdParseData")
public class ThirdParseData implements ParseData {
    private static final String curFlag = "3";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public ThirdParseData() {
    }

    public ThirdParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String nu;
        String url = "";
        String flag = page.getRequest().getHeaders().get("referer");
        if (curFlag.equals(flag)) {
            /*此时通过验证可以进行解析*/
            this.parseData4Page(page);
        } else {
            //将当期页面的搜索条件解析并且缓存起来
            this.dealwithSearchCondition(page);
            /*处理地址列表*/
            page.setSkip(true);
            Selectable st = page.getHtml().xpath(xpathUrlStr);
            List<Selectable> st4Urls = st.nodes();
            for (int i = 0; i < st4Urls.size(); i++) {
                Selectable eachSel = st4Urls.get(i);
                url = eachSel.toString();
                request = new Request(url);
                request.addHeader("referer", "3");
                page.addTargetRequest(request);
            }
            st = page.getHtml().xpath(xpathNextPageStr);
            if (st.nodes().size() > 0) {
                nu = st.toString();
                /*处理下一页*/
                if (!StringUtil.isBlank(nu) && nu.contains("http")) {
                    request = new Request(nu);
                    page.addTargetRequest(request);
                }
            }

        }
    }

    @Override
    public void parseData4Page(Page page) {
        /* 将唯一键放入字段中*/
        page.putField("url", page.getUrl().toString());
        /* 将第一页中的搜索相关字段 保存*/
        page.putField("source", cloudDataCollect.getSource());
        page.putField("search_area", cloudDataCollect.getSearch_area());
        page.putField("search_price", cloudDataCollect.getSearch_price());
        page.putField("search_house_type", cloudDataCollect.getSearch_house_type());
        page.putField("search_method", cloudDataCollect.getSearch_method());
        page.putField("search_type", cloudDataCollect.getSearch_type());
        page.putField("search_house_derection", cloudDataCollect.getSearch_house_derection());
        page.putField("search_near_subway", cloudDataCollect.getSearch_near_subway());
        page.putField("search_has_lift", cloudDataCollect.getSearch_has_lift());
        /* 处理第二中详情中字段*/
        //name
        Selectable sel = page.getHtml().xpath("/html/body/div[3]/h3/text()");
        page.putField("name", sel.toString());
        //house_info
        sel = page.getHtml().xpath("/html/body/div[3]/div[2]/div[1]/ul[contains(@class,'house-info-zufang')]/li");
        this.completeHouseInfoFields(page, sel);
      //flush_time
        sel=page.getHtml().xpath("/html/body/div[3]/div[2]/div[1]/div[2]/div/text()");
        if(!StringUtil.isBlank(sel.toString())){
            page.putField("flush_time",sel.toString().substring(sel.toString().indexOf("发布时间：")+5));
        }
        //pics
       //抽取室内图
        StringBuilder picStr=new StringBuilder("");
        sel=page.getHtml().xpath("//*[@id=\"room_pic_wrap\"]/div/img/@data-src");
        List<Selectable> sel4Pics=sel.nodes();
        for (int i = 0; i < sel4Pics.size(); i++) {
            Selectable eachPic =  sel4Pics.get(i);
            if(!StringUtil.isBlank(eachPic.toString())){
                picStr.append(";"+eachPic.toString());
            }
        }
        //户型
        sel=page.getHtml().xpath("//*[@id=\"hx_pic_wrap\"]/div/img/@data-src");
        sel4Pics=sel.nodes();
        for (int i = 0; i < sel4Pics.size(); i++) {
            Selectable eachPic =  sel4Pics.get(i);
            if(!StringUtil.isBlank(eachPic.toString())){
                picStr.append(";"+eachPic.toString());
            }
        }
        //环境
        sel=page.getHtml().xpath("//*[@id=\"surround_pic_wrap\"]/div/img/@data-src");
        sel4Pics=sel.nodes();
        for (int i = 0; i < sel4Pics.size(); i++) {
            Selectable eachPic =  sel4Pics.get(i);
            if(!StringUtil.isBlank(eachPic.toString())){
                picStr.append(";"+eachPic.toString());
            }
        }
        page.putField("pics",picStr.toString());
    }
    @Override
    public void dealwithSearchCondition(Page page) {
        /*将当期网站标示存储起来*/
        cloudDataCollect.setSource(Website.AN_JU_KE_RENT.getDesc());
        /*处理主要的搜索条件*/
        Selectable sel4temp = page.getHtml().xpath("/html/body//div[contains(@class,'items-list')]/div[contains(@class,'items')]");
        List<Selectable> items = sel4temp.nodes();
        for (int i = 0; i < items.size(); i++) {
            Selectable eachItem = items.get(i);
            if (eachItem.toString().contains("位置：")) {
                String areaStr = "";
                sel4temp = eachItem.xpath("//div[contains(@class,'sub-items')]/a[contains(@class,'selected-item')]/text()");
                List<Selectable> areas = sel4temp.nodes();
                for (int j = 0; j < areas.size(); j++) {
                    Selectable selectable = areas.get(j);
                    if (!selectable.toString().contains("全部") && j > 0) {
                        areaStr += "/" + selectable.toString();
                    } else if (!selectable.toString().contains("全部") && j == 0) {
                        areaStr += selectable.toString();
                    }
                }
                cloudDataCollect.setSearch_area(areaStr);
            } else if (eachItem.toString().contains("租金：")) {
                sel4temp = eachItem.xpath("span[contains(@class,'elems-l')]/a[contains(@class,'selected-item')]/text()");
                if (!"全部".equals(ParseUtil.selfTrim(sel4temp.toString()))) {
                    cloudDataCollect.setSearch_price(ParseUtil.selfTrim(sel4temp.toString()));
                }
            } else if (eachItem.toString().contains("房型：")) {
                sel4temp = eachItem.xpath("span[contains(@class,'elems-l')]/a[contains(@class,'selected-item')]/text()");
                if (!"全部".equals(ParseUtil.selfTrim(sel4temp.toString()))) {
                    cloudDataCollect.setSearch_house_type(ParseUtil.selfTrim(sel4temp.toString()));
                }
            } else if (eachItem.toString().contains("类型：")) {
                sel4temp = eachItem.xpath("span[contains(@class,'elems-l')]/a[contains(@class,'selected-item')]/text()");
                if (!"全部".equals(ParseUtil.selfTrim(sel4temp.toString()))) {
                    cloudDataCollect.setSearch_method(ParseUtil.selfTrim(sel4temp.toString()));
                }
            }
        }
        /* 处理更多筛选*/
        Selectable sel4More = page.getHtml().xpath("//*[@id=\"condmenu\"]/ul/li");
        List<Selectable> items4More = sel4More.nodes();
        for (int i = 0; i < items4More.size(); i++) {
            Selectable eachLi = items4More.get(i);
            //search_type
            if (!StringUtil.isBlank(eachLi.xpath("//*[@id=\"condhouseage_txt_id\"]/text()").toString()) && !"房屋类型".equals(eachLi.xpath("//*[@id=\"condhouseage_txt_id\"]/text()").toString())) {
                cloudDataCollect.setSearch_type(eachLi.xpath("//*[@id=\"condhouseage_txt_id\"]/text()").toString());
            }
            //search_house_derection
            else if (!StringUtil.isBlank(eachLi.xpath("//*[@id=\"condhouse_orient_txt_id\"]/text()").toString()) && !"朝向".equals(eachLi.xpath("//*[@id=\"condhouse_orient_txt_id\"]/text()").toString())) {
                cloudDataCollect.setSearch_house_derection(eachLi.xpath("//*[@id=\"condhouse_orient_txt_id\"]/text()").toString());
            }
            //search_near_subway
            else if ("近地铁".equals(eachLi.xpath("label/text()").toString()) && eachLi.xpath("input").toString().contains("checked")) {
                cloudDataCollect.setSearch_near_subway("1");
            }
            //search_has_lift
            else if ("电梯房".equals(eachLi.xpath("label/text()").toString()) && eachLi.xpath("input").toString().contains("checked")) {
                cloudDataCollect.setSearch_has_lift("1");
            }
        }
    }

    //处理house_info
    public void completeHouseInfoFields(Page page, Selectable selectable) {
        List<Selectable> list = selectable.nodes();
        Selectable sel4Temp;
        String str="";
        for (int i = 0; i < list.size(); i++) {
            Selectable eachLi = list.get(i);
            //price
            if(eachLi.toString().contains("full-line cf")){
                str+=eachLi.xpath("span/em/text()").toString();
                str+=eachLi.xpath("span/text()").toString();
                page.putField("price",str);
            }
            else if(eachLi.toString().contains("户型：")){
                page.putField("house_type",eachLi.xpath("span[contains(@class,'info')]/text()").toString());
            }
            else if(eachLi.toString().contains("面积：")){
                page.putField("area",eachLi.xpath("span[contains(@class,'info')]/text()").toString());
            }
            else if(eachLi.toString().contains("朝向：")){
                page.putField("house_derection",eachLi.xpath("span[contains(@class,'info')]/text()").toString());
            }
            else if(eachLi.toString().contains("楼层：")){
                page.putField("floor",eachLi.xpath("span[contains(@class,'info')]/text()").toString());
            }
            else if(eachLi.toString().contains("装修：")){
                page.putField("decorate",eachLi.xpath("span[contains(@class,'info')]/text()").toString());
            }
            else if(eachLi.toString().contains("类型：")){
                page.putField("type",eachLi.xpath("span[contains(@class,'info')]/text()").toString());
            }
            else if(eachLi.toString().contains("小区：")){
                List<Selectable> selectables=eachLi.xpath("a").nodes();
                if(selectables.size()==1)
                {
                    page.putField("belong_community",ParseUtil.getTextFromHtml(selectables.get(0).toString()));
                }
                else if(selectables.size()==3){
                    page.putField("belong_community",ParseUtil.getTextFromHtml(selectables.get(0).toString()));
                    page.putField("address",ParseUtil.getTextFromHtml(selectables.get(1).toString())+"/"+ParseUtil.getTextFromHtml(selectables.get(2).toString()));
                }
            }
        }

    }
}
