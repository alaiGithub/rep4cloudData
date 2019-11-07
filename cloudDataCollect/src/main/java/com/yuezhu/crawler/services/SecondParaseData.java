package com.yuezhu.crawler.services;

import com.yuezhu.crawler.model.CloudDataCollect;
import com.yuezhu.util.ParseUtil;
import lombok.Data;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Data
@Component("secondParseData")//备用
public class SecondParaseData implements ParseData {
    private static final String curFlag = "2";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public SecondParaseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    public SecondParaseData() {
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
            Selectable st = page.getHtml().xpath(xpathDivStr);
            List<Selectable> st4Urls = st.nodes();
            for (int i = 0; i < st4Urls.size(); i++) {
                request = new Request(url);
                Selectable selectable = st4Urls.get(i);
                url = selectable.xpath(xpathUrlStr).toString();
                request.addHeader("referer", curFlag);
                page.addTargetRequest(request);
            }
            st = page.getHtml().xpath(xpathNextPageStr);
            if (st.nodes().size() > 0) {
                st = st.nodes().get(st.nodes().size() - 1);//取出最后一个
                nu = ParseUtil.getHrefStr(st.toString());
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
        /*将首页中的搜索字段放入page中 */
        page.putField("source", cloudDataCollect.getSource());
        page.putField("search_area", cloudDataCollect.getSearch_area());
        page.putField("search_type", cloudDataCollect.getSearch_type());
        page.putField("search_price", cloudDataCollect.getSearch_price());
        page.putField("search_area_size", cloudDataCollect.getSearch_area_size());
        page.putField("search_special", cloudDataCollect.getSearch_special());
        page.putField("search_house_type", cloudDataCollect.getSearch_house_type());
        page.putField("search_floor_cout", cloudDataCollect.getSearch_floor_cout());
        page.putField("search_decorate", cloudDataCollect.getSearch_decorate());
        page.putField("search_house_age", cloudDataCollect.getSearch_house_age());
        page.putField("search_house_derection", cloudDataCollect.getSearch_house_derection());
        page.putField("search_equity", cloudDataCollect.getSearch_equity());//产权
        page.putField("search_class", cloudDataCollect.getSearch_class());//搜索房子分类
        /*具体解析网页详细内容 并且存储在page中 */
        //name
        Selectable selectable = page.getHtml().xpath("/html/body/div[4]/div[1]/h2/span/text()");
        page.putField("name", selectable.toString());
        //person_info
        selectable = page.getHtml().xpath("/html/body/div[4]/div[1]/div[1]/div[2]");//留意一下
        this.completeField4PersonInfo(page, selectable);
        //contact
        selectable = page.getHtml().xpath("/html/body/div[4]/div[2]/div[1]/div[2]/span/text()");
        page.putField("concat", selectable.toString());
        //flush_time
        selectable = page.getHtml().xpath("/html/body/div[4]/div[1]/p/span[2]/text()");
        page.putField("flush_time", selectable.toString());
        //pics
        selectable = page.getHtml().xpath("//*[@id=\"fytp_cont\"]/div/div[contains(@class,'imageDiv')]/img/@src");
        List<Selectable> sel4Imgs = selectable.nodes();
        String picsStr = "";
        for (int i = 0; i < sel4Imgs.size(); i++) {
            Selectable eachSel = sel4Imgs.get(i);
            if (i == 0) {
                picsStr += eachSel.toString();
            } else {
                picsStr += ";" + eachSel.toString();
            }
        }
        page.putField("pics", picsStr);

    }

    @Override
    public void dealwithSearchCondition(Page page) {
        String temp = "";
        Selectable sel;
        /*将当期网站标示存储起来*/
        cloudDataCollect.setSource("House365");
        /*将页面的搜索条件存储下来*/
        Selectable sel4MainSearch = page.getHtml().xpath("//*[@id=\"nav_search\"]/div[2]/div[1]/");
        List<Selectable> selectables = sel4MainSearch.nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable eachSel = selectables.get(i);
            //=========================
            if(eachSel.toString().contains("区属：")){
                //处理 所属区域（市区/街道）
                sel = eachSel.xpath("dl/dd[contains(@class,'currentItem')]/a/text()");
                temp = sel.toString();
                String area2 = eachSel.xpath("div/div//a[contains(@class,'on')").toString();
                if ((area2 != null) && (!"任意".equals(area2.replaceAll("<\\/?.+?\\/?>", "")))) {
                    temp += "/" + area2.replaceAll("<\\/?.+?\\/?>", "");
                }
                cloudDataCollect.setSearch_area(temp);
            }
            else if(eachSel.toString().contains("类型：")){
                //处理类型
                sel = eachSel.xpath("dl//a[contains(@class,'on')]/text()");
                if (sel.toString() != null && !"全部".equals(sel.toString())) {
                    temp = sel.toString();
                    cloudDataCollect.setSearch_type(temp);
                }

            }
            else if(eachSel.toString().contains("分类：")){
                //处理类型
                sel = eachSel.xpath("dl//a[contains(@class,'on')]/text()");
                if (sel.toString() != null && !"全部".equals(sel.toString())) {
                    temp = sel.toString();
                    cloudDataCollect.setSearch_class(temp);
                }
            }
            else if(eachSel.toString().contains("售价：")){
                //处理售价
                sel = eachSel.xpath("dl//a[contains(@class,'on')]/text()");
                if (sel.toString() != null && !"全部".equals(sel.toString())) {
                    temp = sel.toString();
                    cloudDataCollect.setSearch_price(temp);
                }
            }
            else if(eachSel.toString().contains("面积：")){
                //处理面积
                sel = eachSel.xpath("dl//a[contains(@class,'on')]/text()");
                if (sel.toString() != null && !"全部".equals(sel.toString())) {
                    temp = sel.toString();
                    cloudDataCollect.setSearch_area_size(temp);
                }
            }
            else if(eachSel.toString().contains("房源特色：")){
                //房源特色
                temp = "";
                sel = eachSel.xpath("dl//label[contains(@class,'checkon')]/text()");
                for (int j = 0; j < sel.nodes().size(); j++) {
                    if (j == 0) {
                        temp = sel.nodes().get(j).toString();
                    } else {
                        temp += "/" + sel.nodes().get(j).toString();
                    }
                }
                cloudDataCollect.setSearch_special(temp);
            }
            //=========================
        }
        //处理更多搜索条件
        //================================
        if (page.getHtml().xpath("//*[@id=\"fx_select\"]/div[1]/div[1]/input/@value").get() != null && !"房型".equals(page.getHtml().xpath("//*[@id=\"fx_select\"]/div[1]/div[1]/input/@value").get())) {
            //房型
            cloudDataCollect.setSearch_house_type(page.getHtml().xpath("//*[@id=\"fx_select\"]/div[1]/div[1]/input/@value").get());
        }
        if (page.getHtml().xpath("//*[@id=\"lc_select\"]/div[1]/div[1]/input/@value").get() != null && !"楼层".equals(page.getHtml().xpath("//*[@id=\"lc_select\"]/div[1]/div[1]/input/@value").get())) {
            //楼层
            cloudDataCollect.setSearch_floor_cout(page.getHtml().xpath("//*[@id=\"lc_select\"]/div[1]/div[1]/input/@value").get());
        }
        if (page.getHtml().xpath("//*[@id=\"zx_select\"]/div[1]/div[1]/input/@value").get() != null && !"装修".equals(page.getHtml().xpath("//*[@id=\"zx_select\"]/div[1]/div[1]/input/@value").get())) {
            //装修
            cloudDataCollect.setSearch_decorate(page.getHtml().xpath("//*[@id=\"zx_select\"]/div[1]/div[1]/input/@value").get());
        }
        if (page.getHtml().xpath("//*[@id=\"fl_select\"]/div[1]/div[1]/input/@value").get() != null && !"房龄".equals(page.getHtml().xpath("//*[@id=\"fl_select\"]/div[1]/div[1]/input/@value").get())) {
            //房龄
            cloudDataCollect.setSearch_house_age(page.getHtml().xpath("//*[@id=\"fl_select\"]/div[1]/div[1]/input/@value").get());
        }
        if (page.getHtml().xpath("//*[@id=\"cx_select\"]/div[1]/div[1]/input/@value").get()!= null && !"朝向".equals(page.getHtml().xpath("//*[@id=\"cx_select\"]/div[1]/div[1]/input/@value").get())) {
            //朝向
            cloudDataCollect.setSearch_house_derection(page.getHtml().xpath("//*[@id=\"cx_select\"]/div[1]/div[1]/input/@value").get());
        }
        if (page.getHtml().xpath("//*[@id=\"cq_select\"]/div[1]/div[1]/input/@value").get()!= null && !"产权".equals(page.getHtml().xpath("//*[@id=\"cq_select\"]/div[1]/div[1]/input/@value").get())) {
            //产权
            cloudDataCollect.setSearch_equity(page.getHtml().xpath("//*[@id=\"cq_select\"]/div[1]/div[1]/input/@value").get());
        }
        //================================
    }

    public void completeField4PersonInfo(Page page, Selectable selectable) {
        Selectable sel = selectable.xpath("div[contains(@class,'gr_table')]/dl[contains(@class,'fl')]");
        Selectable sel4temp;
        List<Selectable> sel4dls = sel.nodes();
        for (int i = 0; i < sel4dls.size(); i++) {
            Selectable eachDl = sel4dls.get(i);
            if (eachDl.toString().contains("售价：")) {
                String priceStr = eachDl.xpath("dd/span/i[1]/text()").toString();
                String unitStr = eachDl.xpath("dd/span/text()").toString();
                page.putField("price", priceStr + "" + unitStr);
                sel4temp = eachDl.xpath("dd/span/i[2]/text()");
                page.putField("unit_price", sel4temp.toString());
            } else if (eachDl.toString().contains("税费：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("tax", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("税费方式：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("tax_method", ParseUtil.selfTrim(sel4temp.toString()));
            }
            else if (eachDl.toString().contains("面积：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("area", ParseUtil.selfTrim(sel4temp.toString()));
            }
            else if (eachDl.toString().contains("物业费：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("property_price", ParseUtil.selfTrim(sel4temp.toString()));
            }
            else if (eachDl.toString().contains("户型：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("house_type", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("楼层：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("floor", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("朝向：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("house_derection", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("类型：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("type", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("装修：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("decorate", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("权属：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("ownership", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("年代：")) {
                sel4temp = eachDl.xpath("dd/text()");
                page.putField("house_age", ParseUtil.selfTrim(sel4temp.toString()));
            } else if (eachDl.toString().contains("小区：")) {
                sel4temp = eachDl.xpath("dd/a[1]/text()");
                //特殊情况兼容一下
                if(sel4temp.toString()==null){
                    sel4temp=eachDl.xpath("dd/span/text()");
                    page.putField("belong_community", sel4temp.toString());
                }
                else {
                    page.putField("belong_community", sel4temp.toString());
                    sel4temp = eachDl.xpath("dd/a[2]/text()");
                    page.putField("address", ParseUtil.selfTrim(sel4temp.toString())+"/"+ParseUtil.selfTrim(eachDl.xpath("dd/a[3]/text()").toString()));
                }
               /* String compStr = ParseUtil.getTextFromHtml(sel4temp.toString());
                if(!StringUtil.isBlank(compStr)){
                    page.putField("belong_community", compStr.substring(0, compStr.indexOf("（")));
                    page.putField("address", compStr.substring(compStr.indexOf("（")+1,compStr.indexOf("）")));
                }*/
            }
        }
        //电话号码
        sel = selectable.xpath("div[contains(@class,'gr_phone_div')]//p[contains(@class,'num')]/text()");
        page.putField("telephone", sel.toString());
    }
}
