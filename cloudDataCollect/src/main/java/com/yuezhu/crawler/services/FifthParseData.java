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
public class FifthParseData implements ParseData {
    private static final String curFlag = "5";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public FifthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    public FifthParseData() {
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
                request.addHeader("referer", "5");
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
        /* 导入首页的查询字段*/
        page.putField("source", Website.WU_BA_SP_RENT_SALE.getDesc());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_rent",cloudDataCollect.getSearch_rent());
        page.putField("search_price",cloudDataCollect.getSearch_price());
        page.putField("search_area_size",cloudDataCollect.getSearch_area_size());
        page.putField("search_special",cloudDataCollect.getSearch_special());
        page.putField("search_type",cloudDataCollect.getSearch_type());
        page.putField("search_resource",cloudDataCollect.getSearch_resource());
        /* 处理子页面中的字段*/
        //name
        Selectable temp4Sel;
        String temp4Str;
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/h1/text()");
        page.putField("name",temp4Sel.toString());
        //flush_time
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/p/span[1]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            page.putField("flush_time",temp4Sel.toString().replaceAll("[^\\d\\-]",""));
        }
        //price
        temp4Str="";
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/p[1]/span[1]/text()");
        if (!StringUtil.isBlank(temp4Sel.toString())){
            temp4Str+=temp4Sel.toString();
        }
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/p[1]/span[2]/text()");
        if (!StringUtil.isBlank(temp4Sel.toString())){
            temp4Str+=temp4Sel.toString();
        }
        page.putField("price",temp4Str);
        //unit_price
        temp4Str="";
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/p[1]/span[3]/text()");
        if (!StringUtil.isBlank(temp4Sel.toString())){
            temp4Str+=temp4Sel.toString();
        }
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/p[1]/span[4]/text()");
        if (!StringUtil.isBlank(temp4Sel.toString())){
            temp4Str+=temp4Sel.toString();
        }
        page.putField("unit_price",temp4Str);
        //concat
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/div/a/text()");
        page.putField("concat",temp4Sel.toString());
        //telephone
        temp4Sel=page.getHtml().xpath("//*[@id=\"houseChatEntry\"]/div/p[1]/text()");
        page.putField("telephone",temp4Sel.toString());
        //pics
        StringBuilder sb=new StringBuilder();
        temp4Sel=page.getHtml().xpath("//*[@id=\"generalType\"]/div[1]/ul/li/img/@src");
        List<Selectable> sel4Srcs=temp4Sel.nodes();
        for (Selectable eachSrc:
             sel4Srcs) {
            sb.append(";"+eachSrc.toString());
        }
        page.putField("pics",sb.toString());
        //main_info
        List<Selectable> sel4Lis=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li").nodes();
        for (Selectable eachLi:sel4Lis
             ) {
            temp4Str=eachLi.toString();
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("面积:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"面积:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("area",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("类型:")
                    &&!temp4Str.replaceAll("&nbsp;|\\s","").contains("车位类型:")
            ){
                String innerStr;
                innerStr=eachLi.xpath("span/a[contains(@class,'blue-link')]/text()").toString();
                page.putField("type",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("楼层:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"楼层:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("floor",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("规格:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"规格:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_standard",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("状态:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"状态:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_status",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("预期收益:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"预期收益:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_expect_profit",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("预期收益:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"预期收益:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_expect_profit",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("租赁方式:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"租赁方式:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("rent_method",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("首层层高:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"首层层高:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("factory_oneFloor_high",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("首付:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"首付:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("factory_one_pay",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("所有权:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"所有权:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("ownership",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("规划用途:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"规划用途:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("land_plan_apply",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("付款方式:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"付款方式:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_pay_method",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("车位类型:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"车位类型:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("park_type",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("经营行业:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"经营行业:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_manage_industry",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("起租期:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"起租期:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_rent_begTime",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("客流:")){
                String innerStr;
                innerStr=ParseUtil.getContWithReg(temp4Str,"客流:</span>","</span>");
                innerStr=ParseUtil.getTextFromHtml(innerStr);
                page.putField("shop_passenger_flow",innerStr);
            }
            if(!StringUtil.isBlank(temp4Str)&&temp4Str.replaceAll("&nbsp;|\\s","").contains("位置:")){
               List<Selectable> sel4Links=eachLi.xpath("a[contains(@class,'blue-link')]/text()").nodes();
               temp4Str="";
                for (Selectable eachLink:
                        sel4Links) {
                    temp4Str+="/"+eachLink.toString();
                }
                temp4Sel=eachLi.xpath("span[contains(@class,'xxdz-des')]/text()");
                temp4Str+="/"+temp4Sel.toString();
                page.putField("address",temp4Str);
            }
        }

    }

    @Override
    public void dealwithSearchCondition(Page page) {
      Selectable selectable=page.getHtml().xpath("/html/body/div[5]/div[contains(@class,'filter-wrap')]/dl");
      List<Selectable> sel4Li=selectable.nodes();
      String tempStr;
        for (Selectable eachLi: sel4Li) {
            //search_area
            if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("区域")){
                if (!StringUtil.isBlank(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString())){
                    cloudDataCollect.setSearch_area(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_area=>sub
            else if(StringUtil.isBlank(eachLi.xpath("dt/text()").toString())){
                if (!StringUtil.isBlank(eachLi.xpath("dd/div/a[contains(@class,'select')]/text()").toString())){
                    cloudDataCollect.setSearch_area(cloudDataCollect.getSearch_area()+"/"+eachLi.xpath("dd/div/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_rent
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("租金")){
                tempStr=eachLi.xpath("dd/span[contains(@class,'show')]/a[contains(@class,'select')]/text()").toString();
                if (checkSelIsMean(eachLi.xpath("dd/span[contains(@class,'show')]/a[contains(@class,'select')]/text()"))){
                    if(tempStr.contains("天")){
                        cloudDataCollect.setSearch_unit_price(tempStr);
                    }
                    else if(tempStr.contains("月")){
                        cloudDataCollect.setSearch_price(tempStr);
                    }
                    else{
                        cloudDataCollect.setSearch_price(tempStr);
                    }
                }
            }
            //search_price
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("售价")){
                tempStr=eachLi.xpath("dd/span[contains(@class,'show')]/a[contains(@class,'select')]/text()").toString();
                if (checkSelIsMean(eachLi.xpath("dd/span[contains(@class,'show')]/a[contains(@class,'select')]/text()"))){
                    cloudDataCollect.setSearch_price(tempStr);
                }
            }
            //search_area_size
           else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("面积")){
                if (checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'select')]/text()"))){
                    cloudDataCollect.setSearch_area_size(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_special==>抽取的和浏览器不符，留意一下
           else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("特色")){
                List<Selectable> sel4spans=eachLi.xpath("dd/span").nodes();
                String specialStr="";
                for (Selectable eachSpan :
                        sel4spans ) {
                    if(!StringUtil.isBlank(eachSpan.toString())&&!eachSpan.toString().contains("unchecked")){
                        specialStr+="/"+ ParseUtil.getContWithReg(eachSpan.toString(),"</em>","</span>");
                    }
                }
                cloudDataCollect.setSearch_special(specialStr);
            }
            //search_other
          else  if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("其他")){
                List<Selectable> sel4divs=eachLi.xpath("dd/div/div").nodes();
                String specialStr="";
                for (Selectable each4div :
                        sel4divs ) {
                    if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("类型：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("类型不限")){
                            cloudDataCollect.setSearch_type(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                    else if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("来源：")){
                    if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("来源不限")){
                        cloudDataCollect.setSearch_resource(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                    }
                    }
                }
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
