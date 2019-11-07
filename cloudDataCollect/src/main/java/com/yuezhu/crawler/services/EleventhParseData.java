package com.yuezhu.crawler.services;

import com.yuezhu.crawler.enums.Website;
import com.yuezhu.crawler.model.CloudDataCollect;
import com.yuezhu.util.ParseUtil;
import lombok.Data;
import org.jsoup.helper.StringUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Data
public class EleventhParseData implements ParseData {
    private static final String curFlag = "11";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static String domainStr;
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public EleventhParseData() {
    }

    public EleventhParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String url = "";
        String flag = page.getRequest().getHeaders().get("referer");
        //取出当前页中的访问域名
        String domain=page.getUrl().toString().substring(0,page.getUrl().toString().indexOf(".com")+5);
        this.domainStr=domain;
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
                request = new Request(domainStr+url);
                request.addHeader("referer", "11");
                page.addTargetRequest(request);
            }
            /*处理下一页*/
            String nxtStr="";
            List<Selectable> sel4nxts=page.getHtml().xpath(xpathNextPageStr).nodes();
            for (Selectable eachLink:
                sel4nxts ) {
                if(eachLink.toString().contains("下一页")){
                    nxtStr=domainStr+ ParseUtil.getContWithReg(eachLink.toString(),"href=\"","\">");
                    break;
                }

            }
            if(!StringUtil.isBlank(nxtStr)){
                request = new Request(nxtStr);
                page.addTargetRequest(request);
            }
        }
    }
    @Override
    public void parseData4Page(Page page) {
        /* 将唯一键放入字段中*/
        page.putField("url", page.getUrl().toString());
        /* 导入首页中查询相关的条件*/
        page.putField("source", Website.FANG_TIAN_XIA_ES_SALE.getDesc());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_price",cloudDataCollect.getSearch_price());
        page.putField("search_house_type",cloudDataCollect.getSearch_house_type());
        page.putField("search_area_size",cloudDataCollect.getSearch_area_size());
        page.putField("search_special",cloudDataCollect.getSearch_special());
        page.putField("search_derection",cloudDataCollect.getSearch_house_derection());
        page.putField("search_house_age",cloudDataCollect.getSearch_house_age());
        page.putField("search_floor_cout",cloudDataCollect.getSearch_floor_cout());
        page.putField("search_decorate",cloudDataCollect.getSearch_decorate());
        page.putField("search_class",cloudDataCollect.getSearch_class());
        /*处理主要的详情相关的字段*/
        //name
        Selectable temp4Sel;
        String temp4Str;
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/div[1]/h1/text()");
        page.putField("name",temp4Sel.toString());
        //flush_time
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[1]/div[1]/div[2]/div[4]/span[2]/text()");
        page.putField("flush_time",temp4Sel.toString());
        //price and factory_one_pay
      Selectable   priceAndPay=page.getHtml().xpath("/html/body/div[4]/div[1]/div[4]/div[1]/div[1]");
        temp4Str="";
        temp4Str+=priceAndPay.xpath("div[contains(@class,'price_esf')]/i/text()").toString()+priceAndPay.xpath("div[contains(@class,'price_esf')]/text()").toString();
        page.putField("price",temp4Str);
        temp4Str=priceAndPay.xpath("div[contains(@class,'rel')]/div[1]/text()").toString();
        page.putField("factory_one_pay",temp4Str);//先放一下
        //pics
        StringBuffer sb=new StringBuffer();
        List<Selectable> sel4Pics=page.getHtml().xpath("//*[@id=\"dsdateildesimgs\"]/div/img/@data-src").nodes();
        for (Selectable eachSrc:
             sel4Pics) {
            sb.append(";"+eachSrc.toString());
        }
        page.putField("pics",sb.toString());
        //mainInfo_1
        List<Selectable> sel4Maininfo1=page.getHtml().xpath("/html/body/div[4]/div[1]/div[4]//div[contains(@class,'trl-item1')]").nodes();
        for (Selectable eachItem:
           sel4Maininfo1  ) {
            if(eachItem.xpath("div[contains(@class,'font14')]/text()").toString().contains("户型")){
            page.putField("house_type",eachItem.xpath("div[contains(@class,'tt')]/text()").toString());
            }
            else if(eachItem.xpath("div[contains(@class,'font14')]/text()").toString().contains("建筑面积")){
            page.putField("area",eachItem.xpath("div[contains(@class,'tt')]/text()").toString());
            }
            else if(eachItem.xpath("div[contains(@class,'font14')]/text()").toString().contains("单价")){
            page.putField("unit_price",eachItem.xpath("div[contains(@class,'tt')]/text()").toString());
            }
            else if(eachItem.xpath("div[contains(@class,'font14')]/text()").toString().contains("朝向")){
            page.putField("house_derection",eachItem.xpath("div[contains(@class,'tt')]/text()").toString());
            }
            else if(eachItem.xpath("div[contains(@class,'font14')]/text()").toString().contains("楼层")){
                temp4Str=eachItem.xpath("div[contains(@class,'font14')]/text()").toString();
                temp4Str=temp4Str.substring(temp4Str.indexOf("（"),temp4Str.indexOf("）")+1);
            page.putField("floor",eachItem.xpath("div[contains(@class,'tt')]/text()").toString()+temp4Str);
            }
            else if(eachItem.xpath("div[contains(@class,'font14')]/text()").toString().contains("装修")){
            page.putField("decorate",eachItem.xpath("div[contains(@class,'tt')]/text()").toString());
            }
        }
        //belong_community
        page.putField("belong_community",page.getHtml().xpath("//*[@id=\"kesfyzwtxq_A01_01_05\"]/text()").toString());
        //address
        temp4Str="";
        List<Selectable> sel4Links=page.getHtml().xpath("/html/body/div[4]/div[1]/div[4]/div[4]/div[2]/div[2]/a/text()").nodes();
        for (int i = 0; i < sel4Links.size(); i++) {
            Selectable eachLink =  sel4Links.get(i);
           if(i>0){
               temp4Str+="/"+eachLink.toString();
           }
           else{
               temp4Str+=eachLink.toString();
           }
        }
        page.putField("address",temp4Str);
        //concat
        page.putField("concat",page.getHtml().xpath("/html/body/div[4]/div[1]/div[4]/div[5]/div[1]/text()").toString());
        //telephone_two_diamension
        page.putField("telephone_two_diamension",this.domainStr+page.getHtml().xpath("/html/body/div[4]/div[1]/div[4]/div[contains(@class,'yztel')]/div[2]/div/div[1]/img/@src").toString());

    }

    @Override
    public void dealwithSearchCondition(Page page) {
//        page.setCharset("GBK");
        List<Selectable> sel4Lis=page.getHtml().xpath("//*[@id=\"ri010\"]/div[1]/ul/li").nodes();
        Selectable temp4Sel;
        String temp4Str;
        for (Selectable eachLi:
             sel4Lis) {
            if(eachLi.xpath("span/text()").toString()!=null&&eachLi.xpath("span/text()").toString().contains("区域")){
                temp4Sel=eachLi.xpath("ul/li[contains(@class,'on')]/a/text()");
                cloudDataCollect.setSearch_area(temp4Sel.toString());
            }
            else if(StringUtil.isBlank(eachLi.xpath("span/text()").toString())){
                temp4Sel=eachLi.xpath("ul/li[contains(@class,'on')]/a/text()");
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    cloudDataCollect.setSearch_area(cloudDataCollect.getSearch_area()+"/"+temp4Sel.toString());
                }
            }
           else if(eachLi.xpath("span/text()").toString()!=null&&eachLi.xpath("span/text()").toString().contains("总价")){
                List<Selectable> sel4Links=eachLi.xpath("ul/li[contains(@class,'on')]/label/a/text()").nodes();
                temp4Str="";
                for (int i = 0; i < sel4Links.size(); i++) {
                    Selectable eachLink =  sel4Links.get(i);
                    if(i>0){
                        temp4Str+="/"+eachLink.toString();
                    }
                    else {
                        temp4Str+=eachLink.toString();
                    }
                }
                    cloudDataCollect.setSearch_price(temp4Str);
                }
           else if(eachLi.xpath("span/text()").toString()!=null&&eachLi.xpath("span/text()").toString().contains("户型")){
                temp4Str="";
                List<Selectable> sel4Links=eachLi.xpath("ul/li[contains(@class,'on')]/label/a/text()").nodes();
                for (int i = 0; i < sel4Links.size(); i++) {
                    Selectable eachLink =  sel4Links.get(i);
                    if(i>0){
                        temp4Str+="/"+eachLink.toString();
                    }
                    else {
                        temp4Str+=eachLink.toString();
                    }
                }
                    cloudDataCollect.setSearch_house_type(temp4Str);
                }
           else if(eachLi.xpath("span/text()").toString()!=null&&eachLi.xpath("span/text()").toString().contains("面积")){
                temp4Str="";
                List<Selectable> sel4Links=eachLi.xpath("ul/li[contains(@class,'on')]/label/a/text()").nodes();
                for (int i = 0; i < sel4Links.size(); i++) {
                    Selectable eachLink =  sel4Links.get(i);
                    if(i==0){
                        temp4Str+=eachLink.toString();
                    }
                    else {
                        temp4Str+="/"+eachLink.toString();
                    }
                }
                    cloudDataCollect.setSearch_area_size(temp4Str);
                }
           else if(eachLi.xpath("span/text()").toString()!=null&&eachLi.xpath("span/text()").toString().contains("特色")){
                temp4Str="";
                List<Selectable> list=eachLi.xpath("ul/li").nodes();
                for (int i = 0; i < list.size(); i++) {
                    Selectable eachLink =  list.get(i);
                    if(eachLink.xpath("label/span[1]/@class").toString()!=null&&eachLink.xpath("label/span[1]/@class").toString().contains("icon_check on")){
                        temp4Str+=eachLink.xpath("label/span[2]/text()")+"/";
                    }
                }
                    cloudDataCollect.setSearch_special(temp4Str);
                }
            }
        //more condition
        //search_derection
        temp4Sel=page.getHtml().xpath("//*[@id=\"kesfqbfylb_A01_03_09\"]/p/input/@value");
        String mixStr="朝向/房龄/楼层/装修/建筑类别";
        if(!mixStr.contains(temp4Sel.toString())){
            cloudDataCollect.setSearch_house_derection(temp4Sel.toString());
        }
        //search_hosue_age
        temp4Sel=page.getHtml().xpath("//*[@id=\"kesfqbfylb_A01_03_07\"]/p/input/@value");
        if(!mixStr.contains(temp4Sel.toString())){
            cloudDataCollect.setSearch_house_age(temp4Sel.toString());
        }
        //search_floor_cout
        temp4Sel=page.getHtml().xpath("//*[@id=\"kesfqbfylb_A01_03_03\"]/p/input/@value");
        if(!mixStr.contains(temp4Sel.toString())){
            cloudDataCollect.setSearch_floor_cout(temp4Sel.toString());
        }
        //search_decorate
        temp4Sel=page.getHtml().xpath("//*[@id=\"kesfqbfylb_A01_03_04\"]/p/input/@value");
        if(!mixStr.contains(temp4Sel.toString())){
            cloudDataCollect.setSearch_decorate(temp4Sel.toString());
        }
        //search_class
        temp4Sel=page.getHtml().xpath("//*[@id=\"kesfqbfylb_A01_03_11\"]/p/input/@value");
        if(!mixStr.contains(temp4Sel.toString())){
            cloudDataCollect.setSearch_class(temp4Sel.toString());
        }
        System.out.println(cloudDataCollect);
        }
    }