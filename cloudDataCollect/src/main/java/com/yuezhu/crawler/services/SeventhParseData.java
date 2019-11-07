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
public class SeventhParseData implements ParseData {
    private static final String curFlag = "7";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public SeventhParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    public SeventhParseData() {
    }

    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String url = "";
        String flag = page.getRequest().getHeaders().get("referer");
        if (curFlag.equals(flag)) {
            /*此时通过验证可以进行解析*/
//            System.out.println(page.getHtml().toString());
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
                request.addHeader("referer", "7");
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
        /* 导入首页中查询相关的字段*/
        page.putField("source",Website.WU_BA_ES_SALE.getDesc());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_price",cloudDataCollect.getSearch_price());
        page.putField("search_area_size",cloudDataCollect.getSearch_area_size());
        page.putField("search_house_type",cloudDataCollect.getSearch_house_type());
        page.putField("search_house_derection",cloudDataCollect.getSearch_house_derection());
        page.putField("search_floor_cout",cloudDataCollect.getSearch_floor_cout());
        page.putField("search_equity",cloudDataCollect.getSearch_equity());
        page.putField("search_type",cloudDataCollect.getSearch_type());
        page.putField("search_decorate",cloudDataCollect.getSearch_decorate());
        page.putField("search_house_age",cloudDataCollect.getSearch_house_age());
        /* 完善子页面中的相关的字段*/
        //font_base64
       Selectable sel4Base64=page.getHtml().xpath("/html/head/script[1]");
        String str4fontBase64=ParseUtil.getContWithReg(sel4Base64.toString(),"base64,","'\\)");
//        page.putField("font_base64",str4fontBase64);
        //name
        Selectable temp4Sel;
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/h1/text()");
       /* if(StringUtil.isBlank(temp4Sel.toString())){
            System.out.println(page.getUrl().toString()+">>>>>>"+page.getHtml().toString());
        }*/
        page.putField("name",temp4Sel.toString());
        //flush_time
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/p/span[1]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            page.putField("flush_time",ParseUtil.formateTimeNotRegul(temp4Sel.toString()));
        }
       //belong_community
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li[1]/span[2]/text()");
        page.putField("belong_community",temp4Sel.toString());
        //address
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/a[1]/text()");
        page.putField("address",temp4Sel.toString());
        //concat
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/div[2]/p/text()");//有问题暂时放一下
        page.putField("concat",temp4Sel.toString());
        //telephone
        temp4Sel=page.getHtml().xpath("//*[@id=\"houseChatEntry\"]/div/p[contains(@class,'phone-num')]/text()");
        page.putField("telephone",temp4Sel.toString());
        //overview
        List<Selectable> temp4Overview=page.getHtml().xpath("//*[@id=\"generalSituation\"]/div/ul/li").nodes();
        for (Selectable eachLi:
             temp4Overview) {
            if(eachLi.toString().contains("房屋总价")){
                if(!StringUtil.isBlank(str4fontBase64)){
                    page.putField("price",ParseUtil.batchReplace(eachLi.xpath("span[contains(@class,'c_000')]/text()").toString(),str4fontBase64));
                }
            }
           else if(eachLi.toString().contains("所在楼层")){
                page.putField("floor",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
           else if(eachLi.toString().contains("房屋户型")){
                page.putField("house_type",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
           else if(eachLi.toString().contains("装修情况")){
                page.putField("decorate",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
           else if(eachLi.toString().contains("房本面积")){
                page.putField("area",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
           else if(eachLi.toString().contains("产权年限")){
                page.putField("ownership",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
           else if(eachLi.toString().contains("房屋朝向")){
                page.putField("house_derection",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
        }
        //price_about 费用相关
        List<Selectable> temp4Expense=page.getHtml().xpath("//*[@id=\"generalExpense\"]/div/ul/li").nodes();
        for (Selectable eachLi:
                temp4Expense) {
            if(eachLi.toString().contains("参考首付")){
                page.putField("factory_one_pay",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
           else if(eachLi.toString().contains("房屋类型")){
                page.putField("type",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
        }
        //pics
        List<Selectable> sel4Pics=page.getHtml().xpath("//*[@id=\"generalType\"]/div/ul/li/img/@data-src").nodes();
        StringBuffer sb=new StringBuffer();
        for (Selectable eachSrc:
            sel4Pics ) {
            sb.append(";"+eachSrc.toString());
        }
          page.putField("pics",sb.toString());
    }

    @Override
    public void dealwithSearchCondition(Page page) {
        Selectable selectable=page.getHtml().xpath("/html/body/div[5]/div[contains(@class,'filter-wrap')]/dl");
        List<Selectable> sel4Li=selectable.nodes();
        String tempStr;
        for (Selectable eachLi: sel4Li) {
            //search_area
            if(!StringUtil.isBlank(eachLi.xpath("dd/ul/li/a/text()").toString())&&eachLi.xpath("dd/ul/li/a/text()").toString().contains("区域")){
                if (!StringUtil.isBlank(eachLi.xpath("dd/div[contains(@id,'qySelectFirst')]/a[contains(@class,'select')]/text()").toString())){
                    cloudDataCollect.setSearch_area(eachLi.xpath("dd/div[contains(@id,'qySelectFirst')]/a[contains(@class,'select')]/text()").toString());
                }
                if (!StringUtil.isBlank(eachLi.xpath("dd/div[contains(@id,'qySelectSecond')]/a[contains(@class,'select')]/text()").toString())){
                    cloudDataCollect.setSearch_area(cloudDataCollect.getSearch_area()+"/"+eachLi.xpath("dd/div[contains(@id,'qySelectSecond')]/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_price
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("总价")){
                tempStr=eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString();
                if (checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'select')]/text()"))){
                    cloudDataCollect.setSearch_price(tempStr);
                }
            }
            //search_area_size
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("面积")){
                if (checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'select')]/text()"))){
                    cloudDataCollect.setSearch_area_size(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_house_type
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("厅室")){
                if (checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'select')]/text()"))){
                    cloudDataCollect.setSearch_house_type(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_other
            else  if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("其他")){
                List<Selectable> sel4divs=eachLi.xpath("dd/div/div[contains(@class,'fake_select_item')]").nodes();
                for (Selectable each4div :
                        sel4divs ) {
                    if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("朝向：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("朝向不限")){
                            cloudDataCollect.setSearch_house_derection(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                    else if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("楼层：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("楼层不限")){
                            cloudDataCollect.setSearch_floor_cout(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                    else if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("产权：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("产权不限")){
                            cloudDataCollect.setSearch_equity(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                    else if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("类型：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("类型不限")){
                            cloudDataCollect.setSearch_type(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                    else if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("装修：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("装修不限")){
                            cloudDataCollect.setSearch_decorate(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                    else if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("房龄：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("房龄不限")){
                            cloudDataCollect.setSearch_house_age(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
                        }
                    }
                }
            }
        }
//        System.out.println(cloudDataCollect);
    }
    private boolean checkSelIsMean(Selectable selectable){
        if(!StringUtil.isBlank(selectable.toString())&&!selectable.toString().contains("不限")&&!selectable.toString().contains("全部")){
            return true;
        }
        return false;
    }
}
