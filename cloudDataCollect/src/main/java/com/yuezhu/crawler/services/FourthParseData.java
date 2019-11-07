package com.yuezhu.crawler.services;

import com.yuezhu.crawler.enums.Website;
import com.yuezhu.crawler.model.CloudDataCollect;
import com.yuezhu.util.ParseUtil;
import lombok.Data;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component("fourthParseData")
@Data
public class FourthParseData implements ParseData {
    private static final String curFlag = "4";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件
    public FourthParseData() {
    }

    public FourthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    @Override
    public void preProcess4Request(Page page) {
        Request request;
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
                request.addHeader("referer", "4");
                page.addTargetRequest(request);
            }
            /*处理下一页*/
            st = page.getHtml().xpath(xpathNextPageStr);
            if (!StringUtil.isBlank(st.toString())&& st.toString().contains("http")) {
                    request = new Request(st.toString());
                    page.addTargetRequest(request);
            }

        }
    }

    @Override
    public void parseData4Page(Page page) {
        /* 将唯一键放入字段中*/
        page.putField("url", page.getUrl().toString());
        /* 将第一页中的搜索相关字段 保存*/
        page.putField("source", cloudDataCollect.getSource());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_rent",cloudDataCollect.getSearch_rent());
        page.putField("search_house_type",cloudDataCollect.getSearch_house_type());
        page.putField("search_method",cloudDataCollect.getSearch_method());
        page.putField("search_house_derection",cloudDataCollect.getSearch_house_derection());
        page.putField("search_decorate",cloudDataCollect.getSearch_decorate());
        /* 处理第二页中的字段*/
        //font_base64
        Selectable   temp4Sel=page.getHtml().xpath("/html/head/script[1]");
        String str4fontBase64=ParseUtil.getContWithReg(temp4Sel.toString(),"base64,","'\\)");
        cloudDataCollect.setFont_base64(str4fontBase64);
        //name
        temp4Sel=page.getHtml().xpath("/html/body/div[contains(@class,'main-wrap')]/div[1]/h1/text()");
        page.putField("name",ParseUtil.batchReplace(temp4Sel.toString(),str4fontBase64));
        //flush_time
        temp4Sel=page.getHtml().xpath("/html/body/div[contains(@class,'main-wrap')]/div[1]/p");
        String timeStr=ParseUtil.getContWithReg(temp4Sel.toString().trim(),">","<em");
        page.putField("flush_time",ParseUtil.formateTimeNotRegul(timeStr));
        //house_basic_info
        temp4Sel=page.getHtml().xpath("/html/body/div[contains(@class,'main-wrap')]/div[2]/div[contains(@class,'fr')]/div[1]/div[1]");
        this.completeFields4HouseInfo(page,temp4Sel);
        //pics
        StringBuilder  picsStr=new StringBuilder("");
        List<Selectable>  srcs=page.getHtml().xpath("//*[@id=\"housePicList\"]/li/img/@lazy_src").nodes();
        for (int i = 0; i < srcs.size(); i++) {
            Selectable selectable =  srcs.get(i);
            picsStr.append(";"+selectable.toString());
        }
        page.putField("pics",picsStr.toString());
        //contact
        temp4Sel=page.getHtml().xpath("//*[@id=\"single\"]/p[1]/a/text()");
        page.putField("concat",temp4Sel.toString());
    }

    @Override
    public void dealwithSearchCondition(Page page) {
        /*将当期网站标示存储起来*/
        cloudDataCollect.setSource(Website.WU_BA_RENT_HOUSE.getDesc());
        /* 处理 相关的查询字段*/
        Selectable temp4Sel;
        Selectable selectable=page.getHtml().xpath("/html/body//div[contains(@class,'search_bd')]");
        //search_area
        temp4Sel=selectable.xpath("dl[contains(@class,'secitem secitem_fist')]/dd/a[contains(@class,'select')]/text()");
        List<Selectable> selectables=temp4Sel.nodes();
        String areaStr="";
        for (int i = 0; i < selectables.size(); i++) {
            Selectable each =  selectables.get(i);
            if(!StringUtil.isBlank(each.toString())&&!"不限".equals(each.toString())&&i==0){
                areaStr+=each.toString();
            }
            else if(i>0){
                areaStr+="/"+each.toString();
            }
        }
        cloudDataCollect.setSearch_area(areaStr);
        //search_rent  search_house_type  search_method search_xx
        selectables=selectable.xpath("dl").nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable eachSel =  selectables.get(i);
            if(ParseUtil.checkIsContained("租金：",eachSel.toString())){
                temp4Sel=eachSel.xpath("dd/a[contains(@class,'select')]/text()");
                if (!StringUtil.isBlank(temp4Sel.toString())&&!"不限".equals(temp4Sel.toString())){
                    cloudDataCollect.setSearch_rent(temp4Sel.toString());
                }
            }
            else if(ParseUtil.checkIsContained("厅室：",eachSel.toString())){
                temp4Sel=eachSel.xpath("dd/a[contains(@class,'select')]/text()");
                if (!StringUtil.isBlank(temp4Sel.toString())&&!"不限".equals(temp4Sel.toString())){
                    cloudDataCollect.setSearch_house_type(temp4Sel.toString());
                }
            }
            else if(ParseUtil.checkIsContained("方式：",eachSel.toString())){
                temp4Sel=eachSel.xpath("dd/a[contains(@class,'select')]/text()");
                if (!StringUtil.isBlank(temp4Sel.toString())&&!"不限".equals(temp4Sel.toString())){
                    cloudDataCollect.setSearch_method(temp4Sel.toString());
                }
            }
            else if(ParseUtil.checkIsContained("其他：",eachSel.toString())){
                temp4Sel=eachSel.xpath("dd/div[@id=\"secitem-direction\"]/a[contains(@class,'select')]/text()");
                if (!StringUtil.isBlank(temp4Sel.toString())&&!"朝向不限".equals(temp4Sel.toString())){
                    cloudDataCollect.setSearch_house_derection(temp4Sel.toString());
                }
                temp4Sel=eachSel.xpath("dd/div[@id=\"secitem-decoration\"]/a[contains(@class,'select')]/text()");
                if (!StringUtil.isBlank(temp4Sel.toString())&&!"装修不限".equals(temp4Sel.toString())){
                    cloudDataCollect.setSearch_decorate(temp4Sel.toString());
                }
            }
        }
    }
    private void  completeFields4HouseInfo(Page page,Selectable selectable){
        Selectable temp4Sel=selectable.xpath("div/span[1]/b/text()");
      //price
        String str="";
        if(!StringUtil.isBlank(temp4Sel.toString())){
            str+=temp4Sel.toString()+selectable.xpath("div/span[1]/text()");
        }
        page.putField("price",ParseUtil.batchReplace(str,cloudDataCollect.getFont_base64()));
        //other
        temp4Sel=selectable.xpath("ul/li");
        List<Selectable> selectables=selectable.xpath("ul/li").nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable each =  selectables.get(i);
            if(each.toString().contains("租赁方式：")){
                page.putField("rent_method",ParseUtil.selfTrim(each.xpath("span/text()").nodes().get(1).toString()));
            }
            else if(each.toString().contains("房屋类型：")){
                String fwlxStr=ParseUtil.selfTrim(each.xpath("span/text()").nodes().get(1).toString());
                    page.putField("house_type",ParseUtil.batchReplace(fwlxStr.substring(0,fwlxStr.indexOf("\u00A0")),cloudDataCollect.getFont_base64()));
                    page.putField("area",ParseUtil.batchReplace(fwlxStr.substring(fwlxStr.indexOf(" ")+2,fwlxStr.lastIndexOf("\u00A0")-1),cloudDataCollect.getFont_base64()));
                    page.putField("decorate",fwlxStr.substring(fwlxStr.lastIndexOf("\u00A0")-1));
            }
            else if(each.toString().contains("朝向楼层：")){
                String fwlxStr=ParseUtil.selfTrim(each.xpath("span/text()").nodes().get(1).toString());
                    page.putField("house_derection",fwlxStr.substring(0,fwlxStr.indexOf("\u00A0")));
                    page.putField("floor",ParseUtil.batchReplace(fwlxStr.substring(fwlxStr.indexOf("\u00A0")+1),cloudDataCollect.getFont_base64()));
            }
            else if(each.toString().contains("所在小区：")){
                    page.putField("belong_community",each.xpath("span/a/text()").toString());
            }
            else if(each.toString().contains("所属区域：")){
                List<Selectable> sels=each.xpath("span/a/text()").nodes();
                String addressStr="";
                for (int j = 0; j < sels.size(); j++) {
                    Selectable sel =  sels.get(j);
                    if(j==0){
                        addressStr+=sel.toString();
                    }
                    else {
                        addressStr+="/"+sel.toString();
                    }
                }
                page.putField("address",addressStr);
            }
            else if(each.toString().contains("详细地址：")){
                page.putField("detail_url",each.xpath("span/text()").nodes().get(1).toString());
            }
        }
    }
}
