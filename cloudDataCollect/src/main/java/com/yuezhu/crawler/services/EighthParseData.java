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
public class EighthParseData implements ParseData {
    private static final String curFlag = "8";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件
    public EighthParseData() {
    }

    public EighthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
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
                request = new Request("https://nanjing.fangdd.com"+url);
                request.addHeader("referer", "8");
                page.addTargetRequest(request);
            }
            /*处理下一页*/
            List<Selectable> sel4nxts=page.getHtml().xpath(xpathNextPageStr).nodes();
            String nxtStr="";
            for (int i = 0; i < sel4nxts.size(); i++) {
                Selectable each =  sel4nxts.get(i);
                if(!StringUtil.isBlank(each.xpath("a/i[contains(@class,'icon-arrow-r')]").toString())){
                    nxtStr="https://nanjing.fangdd.com"+each.xpath("a/@href").toString();
                    break;
                }
            }
            if (!StringUtil.isBlank(nxtStr)&& nxtStr.contains("http")) {
                request = new Request(nxtStr);
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void parseData4Page(Page page) {
        /* 将唯一键放入字段中*/
        page.putField("url", page.getUrl().toString());
        /*将首页总查询字段放入page中*/
        page.putField("source", Website.FANG_DD_RENT.getDesc());
        page.putField("search_area", cloudDataCollect.getSearch_area());
        page.putField("search_method", cloudDataCollect.getSearch_method());
        page.putField("search_rent", cloudDataCollect.getSearch_rent());
        page.putField("search_favorable", cloudDataCollect.getSearch_favorable());
        page.putField("search_house_type", cloudDataCollect.getSearch_house_type());
        page.putField("search_area_size", cloudDataCollect.getSearch_area_size());
        page.putField("search_special", cloudDataCollect.getSearch_special());
        page.putField("search_resource", cloudDataCollect.getSearch_resource());
        page.putField("search_house_derection", cloudDataCollect.getSearch_house_derection());
       /* 处理子页面中的字段*/
        Selectable temp;
      //name
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[1]/h1/text()");
        page.putField("name",temp.toString());
        //flush_time
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[1]/p/text()");
        if (!StringUtil.isBlank(temp.toString())){
            page.putField("flush_time",temp.toString().replaceAll("[^0-9\\-]",""));
        }
       //pics
        StringBuilder picStr=new StringBuilder();
        List<Selectable> sele4Pics=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[2]/div[1]/div[1]/div/@style").nodes();
        for (Selectable each: sele4Pics  ) {
            if(!StringUtil.isBlank(each.toString())){
                picStr.append(";"+ParseUtil.getContWithReg(each.toString(),"url\\(","\\)"));
            }
        }
        page.putField("pics",picStr.toString());
        //price
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[1]/div/div[1]/div[1]/div[contains(@class,'KDD-O')]/text()");
        String priceStr="";
        if(temp.toString()!=null){
            priceStr+=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[1]/div/div[1]/div[1]/div[contains(@class,'KDD-O')]/strong/text()").toString()+temp.toString();
        }
        page.putField("price",priceStr);
        //house_type
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[1]/div/div[1]/ul/li[1]/strong/text()");
        page.putField("house_type",temp.toString());
        //area
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[1]/div/div[1]/ul/li[2]/strong/text()");
        page.putField("area",temp.toString());
        //decorate
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[1]/div/div[1]/ul/li[3]/strong/text()");
        page.putField("decorate",temp.toString());
       //basic_detail
        temp=page.getHtml().xpath("//*[@id=\"root\"]/div[2]/div[1]/div[1]/div/div[1]/div[2]/ul/li");
        this.completeFields4InfoList(page,temp);
    }
    @Override
    public void dealwithSearchCondition(Page page) {
      List<Selectable> sel4li=page.getHtml().xpath("//*[@id=\"root\"]/main/div[1]/ul/li").nodes();
      Selectable temp4Sel;
      String str="";
        for (int i = 0; i < sel4li.size(); i++) {
            Selectable each =  sel4li.get(i);
            //search_area==>有问题先放一下
            if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("位置")){
                temp4Sel=each.xpath("div/div[2]/div[1]/a[contains(@class,'active')]/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())&&!temp4Sel.toString().equals("不限")){
                    str+=temp4Sel.toString();
                }
                temp4Sel=each.xpath("div/div[2]/div[contains(@class,'Filter-position-item--sub')]/a[contains(@class,'active')]/text()");
                if(!StringUtil.isBlank(temp4Sel.toString())&&!temp4Sel.toString().equals("不限")){
                    str+="/"+temp4Sel.toString();
                }
                cloudDataCollect.setSearch_area(str);
            }
            //search_method
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("类型")){
            //div/ul/li[contains(@class,'Filter-option-item-on')]
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                if(!StringUtil.isBlank(temp4Sel.toString())){
                  cloudDataCollect.setSearch_method(temp4Sel.toString());
                }
            }
            //search_rent
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("租金")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                if(!StringUtil.isBlank(temp4Sel.toString())){
                  cloudDataCollect.setSearch_rent(temp4Sel.toString());
                }
            }
            //search_favorable
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("优惠")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    List<Selectable> items=temp4Sel.nodes();
                    for (Selectable eachitem:items ) {
                        str+="/"+eachitem.toString();
                    }
                    cloudDataCollect.setSearch_favorable(str);
                }
            }
            //search_house_type
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("户型")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    List<Selectable> items=temp4Sel.nodes();
                    for (Selectable eachitem:items ) {
                        str+="/"+eachitem.toString();
                    }
                    cloudDataCollect.setSearch_house_type(str);
                }
            }
            //search_area_size
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("房屋面积")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    List<Selectable> items=temp4Sel.nodes();
                    for (Selectable eachitem:items ) {
                        str+="/"+eachitem.toString();
                    }
                    cloudDataCollect.setSearch_area_size(str);
                }
            }
            //search_special
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("房源特色")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    List<Selectable> items=temp4Sel.nodes();
                    for (Selectable eachitem:items ) {
                        str+="/"+eachitem.toString();
                    }
                    cloudDataCollect.setSearch_special(str);
                }
            }
            //search_resource
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("房源来源")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    List<Selectable> items=temp4Sel.nodes();
                    for (Selectable eachitem:items ) {
                        str+="/"+eachitem.toString();
                    }
                    cloudDataCollect.setSearch_resource(str);
                }
            }
            //search_house_derection
            else if(!StringUtil.isBlank(each.xpath("strong/text()").toString())&&each.xpath("strong/text()").toString().contains("房屋朝向")){
                temp4Sel=each.xpath("div/ul/li[contains(@class,'Filter-option-item-on')]//span/text()");
                str="";
                if(!StringUtil.isBlank(temp4Sel.toString())){
                    List<Selectable> items=temp4Sel.nodes();
                    for (Selectable eachitem:items ) {
                        str+="/"+eachitem.toString();
                    }
                    cloudDataCollect.setSearch_house_derection(str);
                }
            }

        }
//        System.out.println(cloudDataCollect.toString());
    }
    private  void completeFields4InfoList(Page page,Selectable selectable){
    List<Selectable> selectables=selectable.nodes();
        for (Selectable each4Li:selectables
             ) {
            if(each4Li.toString().contains("类型")){
                page.putField("type",each4Li.xpath("span[contains(@class,'text')]/text()").toString());
            }
            else if(each4Li.toString().contains("朝向")){
                page.putField("house_derection",each4Li.xpath("span[contains(@class,'text')]/text()").toString());
            }
            else if(each4Li.toString().contains("楼层")){
                page.putField("floor",each4Li.xpath("span[contains(@class,'text')]/text()").toString());
            }
            else if(each4Li.toString().contains("看房")){
                page.putField("see_house_occasion",each4Li.xpath("span[contains(@class,'text')]/text()").toString());
            }
            else if(each4Li.toString().contains("宜住")){
                page.putField("suit_stay",each4Li.xpath("span[contains(@class,'text')]/text()").toString());
            }
            else if(each4Li.toString().contains("所在区域")){
               List<Selectable> sel4Addr=each4Li.xpath("span[contains(@class,'text')]/a/text()").nodes();
               String addrStr="";
                for (int i = 0; i < sel4Addr.size(); i++) {
                    Selectable eachAddr =  sel4Addr.get(i);
                    if(i!=0){
                        addrStr+="/"+eachAddr.toString();
                    }
                    else{
                        addrStr+=eachAddr.toString();
                    }
                }
                page.putField("address",addrStr);
            }
        }

    }
}
