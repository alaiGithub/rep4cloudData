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
public class TwelfthParseData implements ParseData {
    private static final String curFlag = "12";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static String domainStr;
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public TwelfthParseData() {
    }

    public TwelfthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String url;
        String flag = page.getRequest().getHeaders().get("referer");
        //取出当前页中的访问域名
        String domain = page.getUrl().toString().substring(0, page.getUrl().toString().indexOf(".com") + 5);
        this.domainStr = domain;
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
                request = new Request(domainStr + url);
                request.addHeader("referer", curFlag);
                page.addTargetRequest(request);
            }
            /*处理下一页*/
            Selectable sel4nxt = page.getHtml().xpath(xpathNextPageStr);
            if (!StringUtil.isBlank(sel4nxt.toString())) {
                request = new Request(domainStr + sel4nxt.toString());
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void parseData4Page(Page page) {
        /* 将唯一键放入字段中*/
        page.putField("url", page.getUrl().toString());
        /* 导入首页中查询相关的条件*/
        page.putField("source", Website.FANG_TIAN_XIA_ZXL_RENT_SALE.getDesc());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_rent",cloudDataCollect.getSearch_rent());
        page.putField("search_area_size",cloudDataCollect.getSearch_area_size());
        page.putField("search_type",cloudDataCollect.getSearch_type());
        page.putField("search_special",cloudDataCollect.getSearch_special());
        /* 完善详情的相关的字段*/
        //name
        Selectable temp4sel;
        String     temp4Str;
        temp4sel=page.getHtml().xpath("/html/body/div[3]/div[6]/div[1]/div[1]/h1/text()");
        page.putField("name",temp4sel.toString());
        //flush_time
        temp4sel=page.getHtml().xpath("/html/body/div[3]/div[6]/div[1]/div[1]/p/text()");
        temp4Str=temp4sel.toString();
        temp4Str=temp4Str.substring(temp4Str.indexOf("发布时间：")+5,temp4Str.indexOf("("));
        temp4Str=temp4Str.replaceAll("/","-");
        page.putField("flush_time",temp4Str);
        //rent_price and shop_pay_method
        temp4sel=page.getHtml().xpath("/html/body/div[3]/div[6]/div[1]/div[3]/div[2]/dl[1]/dt[1]");
        temp4Str= ParseUtil.getTextFromHtml(temp4sel.toString()).replaceAll("\\s","");
        if(temp4Str.contains("单价")){
            page.putField("unit_price",temp4Str.substring(temp4Str.indexOf("单价：")+3,temp4Str.indexOf("（")));
            page.putField("price",temp4Str.substring(temp4Str.indexOf("总价：")+3,temp4Str.indexOf("）")));
        }
        else if(temp4Str.contains("租金")){
            page.putField("price",temp4Str.substring(temp4Str.indexOf("租金：")+3,temp4Str.indexOf("(")));
            page.putField("shop_pay_method",temp4Str.substring(temp4Str.indexOf("支付方式：")+5,temp4Str.indexOf(")")));
            //unit_price
            temp4sel=page.getHtml().xpath("/html/body/div[3]/div[6]/div[1]/div[3]/div[2]/dl[1]/dt[2]");
            temp4Str=ParseUtil.getTextFromHtml(temp4sel.toString()).replaceAll("\\s","");
            page.putField("unit_price",temp4Str);
        }
        //area
        temp4sel=page.getHtml().xpath("/html/body/div[3]/div[6]/div[1]/div[3]/div[2]/dl[1]/dd/text()");
        temp4Str=temp4sel.toString().substring(temp4sel.toString().indexOf("：")+1);
        page.putField("area",temp4Str);
        //concat
        temp4sel=page.getHtml().xpath("//*[@id=\"esfnjxq_06\"]/ul/li/b/text()");
        page.putField("concat",temp4sel.toString());
        //telephone
        temp4sel=page.getHtml().xpath("//*[@id=\"esfnjxq_06\"]/li/b/text()");
        page.putField("telephone",temp4sel.toString());
        //pics
        StringBuffer sb=new StringBuffer();
        List<Selectable> sel4Pics=page.getHtml().xpath("//*[@id=\"house_des\"]/div/div/a/img/@src2").nodes();
        for (Selectable eachSrc:
             sel4Pics) {
            sb.append(";"+eachSrc.toString());
        }
        //pics-2 图片的其他来源
        temp4sel=page.getHtml().xpath("//*[@id=\"thumbnailsw\"]/ul");
        if (temp4sel.toString()!=null){
            //从js中获取图片源
          temp4Str=page.getHtml().xpath("//*[@id=\"house_des\"]/div/script[2]").toString();
          temp4Str=ParseUtil.getContWithReg(temp4Str,"imgJson=",";");
            temp4Str=ParseUtil.getFieldsStrFromJsonStr(temp4Str);
            sb.append(temp4Str);
        }
        page.putField("pics",sb.toString());
        //main_infos
        List<Selectable> mainInfos=page.getHtml().xpath("/html/body/div[3]/div[6]/div[1]/div[3]/div[2]/dl[2]/").nodes();
        for (Selectable eachItem:
             mainInfos) {
            if(eachItem.toString().contains("写字楼名称：")){
              page.putField("bulid_name",ParseUtil.getContWithReg(eachItem.toString(),"</span>","\\(").replaceAll("</?.+?/?>",""));
            }
          else if(eachItem.toString().contains("楼盘地址：")){
              page.putField("detail_url",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dt").replaceAll("</?.+?/?>",""));
            }
          else if(eachItem.toString().contains("楼　　层：")){
              page.putField("floor",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dd"));
            }
          else if(eachItem.toString().contains("物 业 费：")){
              page.putField("property_price",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dd"));
            }
          else if(eachItem.toString().contains("物业公司：")){
              page.putField("property_company",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dd"));
            }
          else if(eachItem.toString().contains("写字楼级别：")){
              page.putField("house_type",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dd"));
            }
          else if(eachItem.toString().contains("装 修：")){
              page.putField("decorate",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dd"));
            }
          else if(eachItem.toString().contains("类 型：")){
              page.putField("type",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dd"));
            }
          else if(eachItem.toString().contains("配套设施：")){
              page.putField("support_facility",ParseUtil.getContWithReg(eachItem.toString(),"</span>","</dt"));
            }
        }
    }

    @Override
    public void dealwithSearchCondition(Page page) {
        Selectable temp4Sel;
        String temp4Str = "";
        //search_area
        temp4Sel = page.getHtml().xpath("//*[@id=\"list_38\"]/div[1]/a[contains(@class,'selected')]/text()");
        temp4Str += temp4Sel.toString();
        temp4Sel = page.getHtml().xpath("//*[@id=\"tagContent0\"]/a[contains(@class,'org bold')]/text()");
        if(temp4Sel.toString()==null){
            temp4Sel = page.getHtml().xpath("//*[@id=\"shangQuancontain\"]/a[contains(@class,'org bold')]/text()");
        }
        if (!StringUtil.isBlank(temp4Sel.toString()) && !temp4Sel.toString().contains("不限")) {
            temp4Str += "/" + temp4Sel.toString();
        }
        cloudDataCollect.setSearch_area(temp4Str);
        //main_search_condition
        List<Selectable>  sel4Lis=page.getHtml().xpath("/html/body//ul[contains(@class,'info ml25')]/li").nodes();
        for (Selectable eachLi:
             sel4Lis) {
            if(eachLi.toString().contains("租金：")){
                temp4Str=eachLi.xpath("p/a[contains(@class,'org bold')]/text()").toString();
                if(!temp4Str.contains("不限")){
                    cloudDataCollect.setSearch_rent(temp4Str);
                }
            }
           else if(eachLi.toString().contains("单价：")){
                temp4Str=eachLi.xpath("p/a[contains(@class,'org bold')]/text()").toString();
                if(!temp4Str.contains("不限")){
                    cloudDataCollect.setSearch_unit_price(temp4Str);
                }
            }
           else if(eachLi.toString().contains("面积：")){
                temp4Str=eachLi.xpath("p/a[contains(@class,'org bold')]/text()").toString();
                if(!temp4Str.contains("不限")){
                    cloudDataCollect.setSearch_area_size(temp4Str);
                }
            }
           else if(eachLi.toString().contains("类型：")){
                temp4Str=eachLi.xpath("a[contains(@class,'org bold')]/text()").toString();
                if(!temp4Str.contains("不限")){
                    cloudDataCollect.setSearch_type(temp4Str);
                }
            }
        }
        //special
        temp4Sel=page.getHtml().xpath("/html/body//div[contains(@class,'moresearchinfo tese')]/a[contains(@class,'org bold')]/text()");
        cloudDataCollect.setSearch_special(temp4Sel.toString());
        System.out.println(cloudDataCollect);
    }
}
