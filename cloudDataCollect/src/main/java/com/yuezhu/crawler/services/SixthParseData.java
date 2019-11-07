package com.yuezhu.crawler.services;

import com.yuezhu.crawler.enums.Website;
import com.yuezhu.crawler.model.CloudDataCollect;
import org.jsoup.helper.StringUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class SixthParseData implements  ParseData {
    private static final String curFlag = "6";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public SixthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    public SixthParseData() {
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
                request.addHeader("referer", curFlag);
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
        page.putField("source", Website.WU_BA_XZL_RENT_SALE.getDesc());
        page.putField("search_area",cloudDataCollect.getSearch_area());
        page.putField("search_type",cloudDataCollect.getSearch_type());
        page.putField("search_area_size",cloudDataCollect.getSearch_area_size());
        page.putField("search_rent",cloudDataCollect.getSearch_rent());
        page.putField("search_price",cloudDataCollect.getSearch_price());
        page.putField("search_special",cloudDataCollect.getSearch_special());
        page.putField("search_decorate",cloudDataCollect.getSearch_decorate());
        page.putField("search_resource",cloudDataCollect.getSearch_resource());
        /* 处理子页面中相关的字段 */
        //name
        Selectable temp4Sel;
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/h1/text()");
        page.putField("name",temp4Sel.toString());
        //flush_time
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[1]/p/span[contains(@class,'up')]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            page.putField("flush_time",temp4Sel.toString().replaceAll("[^\\d\\-]",""));
        }
        //concat
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[2]/div/a/text()");
        page.putField("concat",temp4Sel.toString());
        //telephone
        temp4Sel=page.getHtml().xpath("//*[@id=\"houseChatEntry\"]/div/p[1]/text()");
        page.putField("telephone",temp4Sel.toString());
        //type
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/div[1]/p[3]/span[1]/a/text()");
        page.putField("type",temp4Sel.toString());
        //belong_community
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li[1]/span[2]/span/text()");
        page.putField("belong_community",temp4Sel.toString());
        //detail_url
        String detailStr="";
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/a[1]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            detailStr+=temp4Sel.toString();
        }
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/a[2]/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            detailStr+="/"+temp4Sel.toString();
        }
        temp4Sel=page.getHtml().xpath("/html/body/div[4]/div[2]/div[2]/ul/li[2]/span[2]/span/text()");
        if(!StringUtil.isBlank(temp4Sel.toString())){
            detailStr+="/"+temp4Sel.toString();
        }
        page.putField("detail_url",detailStr);
        //pics
        StringBuilder srcStr=new StringBuilder();
        List<Selectable> srcs=page.getHtml().xpath("//*[@id=\"generalType\"]/div[1]/ul/li/img/@src").nodes();
        for (Selectable eachSrc:
             srcs) {
            if(!StringUtil.isBlank(eachSrc.toString())){
                srcStr.append(";"+eachSrc.toString());
            }
        }
        page.putField("pics",srcStr.toString());
        //overview
        List<Selectable> sel4Lis=page.getHtml().xpath("//*[@id=\"generalSituation\"]/div/ul/li").nodes();
        for (Selectable eachLi:
            sel4Lis ) {
            if(eachLi.toString().contains("写字楼租金")){
                page.putField("price",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("写字楼售价")){
                page.putField("price",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("所在楼层")){
                page.putField("floor",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("建筑面积")){
                page.putField("area",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("装修情况")){
                page.putField("decorate",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("可注册公司")){
                page.putField("can_regit_company",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("使用率")){
                page.putField("use_rate",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("起租期")){
                page.putField("shop_rent_begTime",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("付款方式")){
                page.putField("shop_pay_method",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
            }
            else if(eachLi.toString().contains("物业费")){
                page.putField("property_price",eachLi.xpath("span[contains(@class,'c_000')]/span/text()").toString());
            }
            else if(eachLi.toString().contains("参考容纳工位数")){
                page.putField("suit_stay",eachLi.xpath("span[contains(@class,'c_000')]/text()").toString());
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
            if(!StringUtil.isBlank(eachLi.xpath("dd/ul/li/a/text()").toString())&&eachLi.xpath("dd/ul/li/a/text()").toString().contains("区域")){
                if (!StringUtil.isBlank(eachLi.xpath("dd/div[contains(@id,'qySelectFirst')]/a[contains(@class,'select')]/text()").toString())){
                    cloudDataCollect.setSearch_area(eachLi.xpath("dd/div[contains(@id,'qySelectFirst')]/a[contains(@class,'select')]/text()").toString());
                }
                if (!StringUtil.isBlank(eachLi.xpath("dd/div[contains(@id,'qySelectSecond')]/a[contains(@class,'select')]/text()").toString())){
                    cloudDataCollect.setSearch_area(cloudDataCollect.getSearch_area()+"/"+eachLi.xpath("dd/div[contains(@id,'qySelectSecond')]/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_type
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("类型")){
              if(checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'select')]/text()"))) {
                  cloudDataCollect.setSearch_type(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString());
              }
            }
            //search_area_size
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("面积")){
                if (checkSelIsMean(eachLi.xpath("dd/a[contains(@class,'select')]/text()"))){
                    cloudDataCollect.setSearch_area_size(eachLi.xpath("dd/a[contains(@class,'select')]/text()").toString());
                }
            }
            //search_rent
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("租金")){
                tempStr=eachLi.xpath("dd/span[contains(@class,'show')]/a[contains(@class,'select')]/text()").toString();
                if (checkSelIsMean(eachLi.xpath("dd/span[contains(@class,'show')]/a[contains(@class,'select')]/text()"))){
                    if(tempStr.contains("月")){
                        cloudDataCollect.setSearch_price(tempStr);
                    }
                    else if(tempStr.contains("天")){
                        cloudDataCollect.setSearch_unit_price(tempStr);
                    }
                    else{
                        cloudDataCollect.setSearch_unit_price(tempStr);
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
            //search_special==>抽取的和浏览器不符，留意一下
            else if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("特色")){
                List<Selectable> sel4spans=eachLi.xpath("dd/a[contains(@class,'checkbox checked')]/em[2]/text()").nodes();
                String specialStr="";
                for (Selectable eachSpan :
                        sel4spans ) {
                    if(!StringUtil.isBlank(eachSpan.toString())){
                        specialStr+="/"+ eachSpan.toString();
                    }
                }
                cloudDataCollect.setSearch_special(specialStr);
            }
            //search_other
            else  if(!StringUtil.isBlank(eachLi.xpath("dt/text()").toString())&&eachLi.xpath("dt/text()").toString().contains("其他")){
                List<Selectable> sel4divs=eachLi.xpath("dd/div/div[contains(@class,'fake_select_item handle_item')]").nodes();
                for (Selectable each4div :
                        sel4divs ) {
                    if(!StringUtil.isBlank(each4div.toString())&&each4div.toString().contains("类型：")){
                        if(!StringUtil.isBlank(each4div.xpath("div/a[contains(@class,'select')]/text()").toString())&&!each4div.xpath("div/a[contains(@class,'select')]/text()").toString().contains("装修不限")){
                            cloudDataCollect.setSearch_decorate(each4div.xpath("div/a[contains(@class,'select')]/text()").toString());
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
        if(!StringUtil.isBlank(selectable.toString())&&!selectable.toString().contains("不限")&&!selectable.toString().contains("全部")){
            return true;
        }
        return false;
    }
}
