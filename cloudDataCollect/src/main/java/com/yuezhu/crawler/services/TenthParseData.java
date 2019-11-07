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
public class TenthParseData implements ParseData {
    private static final String curFlag = "10";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件
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
            //取出当前页中的访问域名
            String domainStr=page.getUrl().toString().substring(0,page.getUrl().toString().indexOf(".com")+5);
            Selectable st = page.getHtml().xpath(xpathUrlStr);
            List<Selectable> st4Urls = st.nodes();
            for (int i = 0; i < st4Urls.size(); i++) {
                Selectable eachSel = st4Urls.get(i);
                url = eachSel.toString();
                request = new Request(domainStr+url);
                request.addHeader("referer", "10");
                page.addTargetRequest(request);
            }
            /*处理下一页*/
            List<Selectable> sel4nxts=page.getHtml().xpath(xpathNextPageStr).nodes();
            String nxtStr="";
            for (int i = 0; i < sel4nxts.size(); i++) {
                Selectable each =  sel4nxts.get(i);
                if(each.toString().contains("下一页")){
                    nxtStr=domainStr+ ParseUtil.getContWithReg(each.toString(),"href=\"","\">");
                    break;
                }
            }
            if(!StringUtil.isBlank(nxtStr)){
                request = new Request(nxtStr);
                page.addTargetRequest(request);
            }
        }
    }

    public TenthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    @Override
    public void parseData4Page(Page page) {
        /* 将唯一键放入字段中*/
        page.putField("url", page.getUrl().toString());
        /* 导入首页中查询相关的条件*/
        page.putField("source", Website.FANG_TIAN_XIA_RENT_HOUSE.getDesc());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_rent",cloudDataCollect.getSearch_rent());
        page.putField("search_house_type",cloudDataCollect.getSearch_house_type());
        page.putField("search_method",cloudDataCollect.getSearch_method());
        page.putField("search_house_derection",cloudDataCollect.getSearch_house_derection());
       //name
        Selectable temp4Sel;
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/h1/text()");
        page.putField("name",temp4Sel.toString());
        //flush_time
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/p/span[2]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            page.putField("flush_time",temp4Sel.toString().replaceAll("[^\\d\\-]",""));
        }
        //pics
        List<Selectable> sel4Pics=page.getHtml().xpath("/html/body/div[5]/div[2]/div[1]/div[contains(@class,'content-item')]/div[2]/div/img/@src").nodes();
        StringBuffer sb=new StringBuffer();
        for (Selectable eachSrc:
             sel4Pics) {
            sb.append(";"+eachSrc.toString());
        }
        page.putField("pics",sb.toString());
        //price and shop_pay_method
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[2]/div[1]/i/text()");
        String priceStr=temp4Sel.toString();
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[2]/div[1]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            priceStr+=temp4Sel.toString().substring(0,temp4Sel.toString().indexOf("（"));
            page.putField("price",priceStr);
            page.putField("shop_pay_method",temp4Sel.toString().substring(temp4Sel.toString().indexOf("（")+1,temp4Sel.toString().indexOf("）")));
        }
       //main-info
        List<Selectable> sel4Divs=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[contains(@class,'tr-line clearfix')]/div[contains(@class,'trl-item1')]").nodes();
        for (Selectable eachDiv:
             sel4Divs) {
            if(eachDiv.xpath("div[contains(@class,'font14')]").toString().contains("出租方式")){
                page.putField("rent_method",eachDiv.xpath("div[contains(@class,'tt')]/text()").toString());
            }
           else if(eachDiv.xpath("div[contains(@class,'font14')]").toString().contains("户型")){
                page.putField("house_type",eachDiv.xpath("div[contains(@class,'tt')]/text()").toString());
            }
           else if(eachDiv.xpath("div[contains(@class,'font14')]").toString().contains("建筑面积")){
                page.putField("area",eachDiv.xpath("div[contains(@class,'tt')]/text()").toString());
            }
           else if(eachDiv.xpath("div[contains(@class,'font14')]").toString().contains("朝向")){
                page.putField("house_derection",eachDiv.xpath("div[contains(@class,'tt')]/text()").toString());
            }
           else if(eachDiv.xpath("div[contains(@class,'font14')]").toString().contains("楼层")){
               String floorStr="";
                floorStr=eachDiv.xpath("div[contains(@class,'font14')]/text()").toString().substring(eachDiv.xpath("div[contains(@class,'font14')]/text()").toString().indexOf("（"));
                page.putField("floor",eachDiv.xpath("div[contains(@class,'tt')]/text()").toString()+floorStr);
            }
           else if(eachDiv.xpath("div[contains(@class,'font14')]/text()").toString().contains("装修")){
                page.putField("decorate",eachDiv.xpath("div[contains(@class,'tt')]/text()").toString());
            }
        }
        //belong_community
        String belong_community="";
        List<Selectable> sel4LinkS=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[5]/div[1]/div[2]/a/text()").nodes();
        for (int i = 0; i < sel4LinkS.size(); i++) {
            Selectable eachLink =  sel4LinkS.get(i);
            if(i>0){
                belong_community+="/"+eachLink.toString();
            }
            else{
                belong_community+=eachLink.toString();
            }
        }
        page.putField("belong_community",belong_community);
        //detail_url
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[5]/div[2]/div[2]/a/text()");
        if(StringUtil.isBlank(temp4Sel.toString())){
            temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[5]/div[3]/div[2]/a/text()");
        }
        page.putField("detail_url",temp4Sel.toString());
        //concat
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[6]/div/div[1]/div/a[1]/text()");
        page.putField("concat",temp4Sel.toString());
        //telephone
        temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[1]/div[2]/div[6]/div/div[2]");
        page.putField("telephone",ParseUtil.getTextFromHtml(temp4Sel.toString()));
    }

    @Override
    public void dealwithSearchCondition(Page page) {
      Selectable temp4Sel=page.getHtml().xpath("/html/body/div[5]/div[3]/div[2]/div[1]/dl");
      List<Selectable> sel4Lis=temp4Sel.nodes();
      String temp4Str;
        for (Selectable eachLi:
            sel4Lis ) {
            if(eachLi.xpath("dt/text()").toString().contains("区域")){
                temp4Str="";
                temp4Str+=eachLi.xpath("dd/a[contains(@class,'selected')]/text()").toString();
                temp4Sel=page.getHtml().xpath("//*[@id=\"rentid_D04_08\"]/a[contains(@class,'selected')]/text()");
                if (!StringUtil.isBlank(temp4Sel.toString())){
                    temp4Str+="/"+temp4Sel.toString();
                }
                cloudDataCollect.setSearch_area(temp4Str);
            }
            else if(eachLi.xpath("dt/text()").toString().contains("租金")){
                temp4Str="";
                if(checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'org')]/text()"))){
                    temp4Str+=eachLi.xpath("dd/a[contains(@class,'org')]/text()").toString();
                }
                cloudDataCollect.setSearch_rent(temp4Str);
            }
            else if(eachLi.xpath("dt/text()").toString().contains("户型")){
                temp4Str="";
                if(checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'org')]/text()"))){
                    temp4Str+=eachLi.xpath("dd/a[contains(@class,'org')]/text()").toString();
                }
                cloudDataCollect.setSearch_house_type(temp4Str);
            }
            else if(eachLi.xpath("dt/text()").toString().contains("方式")){
                temp4Str="";
                if(checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'org')]/text()"))){
                    temp4Str+=eachLi.xpath("dd/a[contains(@class,'org')]/text()").toString();
                }
                cloudDataCollect.setSearch_method(temp4Str);
            }
            else if(eachLi.xpath("dt/text()").toString().contains("朝向")){
                temp4Str="";
                if(checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'org')]/text()"))){
                    temp4Str+=eachLi.xpath("dd/a[contains(@class,'org')]/text()").toString();
                }
                cloudDataCollect.setSearch_house_derection(temp4Str);
            }
        }
        System.out.println(cloudDataCollect);
    }
    private boolean checkSelIsMean(Selectable selectable){
        if(!StringUtil.isBlank(selectable.toString())&&!selectable.toString().contains("不限")){
            return true;
        }
        return false;
    }
}
