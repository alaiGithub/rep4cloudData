package com.yuezhu.crawler.services;
import com.yuezhu.crawler.enums.Website;
import com.yuezhu.crawler.model.CloudDataCollect;
import com.yuezhu.util.ParseUtil;
import lombok.Data;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Data
@Component("firstParseData")
public class FirstParseData implements ParseData {
    private static final String curFlag = "1";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String url = "";
        String nu;
        String flag = page.getRequest().getHeaders().get("referer");
        if (null != flag && curFlag.equals(flag)) {
            //此时通过验证可以进行解析
            this.parseData4Page(page);
        } else {
            //处理地址列表
            page.setSkip(true);
            //将当期页面的搜索条件解析并且缓存起来
            this.dealwithSearchCondition(page);
            Selectable st = page.getHtml().xpath(xpathDivStr);//     //*[@id="JS_listPag"]/dd[contains(@class,'listItem')]
            List<Selectable> st4Urls = st.nodes();
            for (int i = 0; i < st4Urls.size(); i++) {
                Selectable selectable = st4Urls.get(i);
                url = selectable.xpath(xpathUrlStr).toString();//div[1]/div/a
                request = new Request(url);
                request.addHeader("referer", curFlag);
                page.addTargetRequest(request);

            }
            st = page.getHtml().xpath(xpathNextPageStr);//html/body/div[3]/form/div/div[3]/div[1]//div[2]/ul//a[contains(@class,'next-page')]
            //处理下一页
            if (0 < st.nodes().size()) {
                nu = st.links().toString();
//                System.out.println("next url:"+nu);
                request = new Request(nu);
                page.addTargetRequest(request);
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
        page.putField("search_resource", cloudDataCollect.getSearch_resource());
        page.putField("search_method", cloudDataCollect.getSearch_method());
        page.putField("search_type", cloudDataCollect.getSearch_type());
        page.putField("search_class",cloudDataCollect.getSearch_class());
        page.putField("search_rent", cloudDataCollect.getSearch_rent());
        page.putField("search_area_size",cloudDataCollect.getSearch_area_size());
        page.putField("search_house_type", cloudDataCollect.getSearch_house_type());
        page.putField("search_floor_cout", cloudDataCollect.getSearch_floor_cout());
        page.putField("search_decorate", cloudDataCollect.getSearch_decorate());
        /*具体解析网页详细内容 并且存储在page中 */
        String temp;
        //name 名称
        Selectable selectable = page.getHtml().xpath("/html/body/div[4]/div[2]/div[1]/div[1]/h1/text()");
        String nameStr = selectable.toString().trim();
        //对nameStr转码
        page.putField("name", nameStr);//留意一下平方米乱码
        //house main info /html/body/div[4]/div[2]/div[1]/div[2]/div[2]
        selectable = page.getHtml().xpath("/html/body/div[4]/div[2]/div[1]/div[2]/div[2]/");
        this.complete4HouseMainInfo(page, selectable);
        //concat
        page.putField("concat", page.getHtml().xpath("//*[@id=\"personal\"]/p[contains(@class,'name')]/text()").toString());
        //pics
        selectable = page.getHtml().xpath("/html/body/div[4]/div[3]/div[1]/div[1]/div[2]//img[contains(@class,'JS_loadImg')]/@src");
        List<Selectable> sel4Imgs = selectable.nodes();
        String picsStr = "";
        for (int i = 0; i < sel4Imgs.size(); i++) {
            Selectable eachImgSrc = sel4Imgs.get(i);
            if (i > 0) {
                picsStr += ";" + eachImgSrc.toString();
            } else {
                picsStr += eachImgSrc.toString();
            }
        }
        page.putField("pics", picsStr);
    }

    @Override
    public void dealwithSearchCondition(Page page) {
        //将当期网站标示存储起来
        cloudDataCollect.setSource(Website.HOUSE365_RENT.getDesc());
        //将页面的搜索条件存储下来
        Selectable st = page.getHtml().xpath("/html/body/div[3]/form/div/div[2]/div[2]/div[1]/div[contains(@class,'list')]");//
        List<Selectable> list = st.nodes();
        String tempStr;
        for (Selectable eachDiv :
                list) {
            if (eachDiv.xpath("p/text()").toString().contains("区域")) {
                tempStr="";
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr += eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                }
                if (!eachDiv.xpath("div[contains(@class,'areaTypeBox')]/dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr +="/"+ eachDiv.xpath("div[contains(@class,'areaTypeBox')]/dl/dd/a[contains(@class,'current')]/text()").toString();
                }
                cloudDataCollect.setSearch_area(tempStr);
            }
            else if(eachDiv.xpath("p/text()").toString().contains("来源")){
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr =eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                    cloudDataCollect.setSearch_resource(tempStr);
                }
            }
            else if(eachDiv.xpath("p/text()").toString().contains("方式")){
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr =eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                    cloudDataCollect.setSearch_method(tempStr);
                }
            }
            else if(eachDiv.xpath("p/text()").toString().contains("类型")){
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr =eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                    cloudDataCollect.setSearch_type(tempStr);
                }
            }
            else if(eachDiv.xpath("p/text()").toString().contains("分类")){
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr =eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                    cloudDataCollect.setSearch_class(tempStr);
                }
            }
            else if(eachDiv.xpath("p/text()").toString().contains("租金")){
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr =eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                    cloudDataCollect.setSearch_rent(tempStr);
                }
            }
            else if(eachDiv.xpath("p/text()").toString().contains("面积")){
                if (!eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString().contains("任意")) {
                    tempStr =eachDiv.xpath("dl/dd/a[contains(@class,'current')]/text()").toString();
                    cloudDataCollect.setSearch_area_size(tempStr);
                }
            }
        }
        /*for (int i = 0; i < list.size(); i++) {
            Selectable urls = list.get(i).xpath("//a[@class='current']/text()");
            if (!StringUtil.isBlank(urls.toString())) {
                filedStr = urls.toString().equals("任意") ? null : urls.toString();
                switch (i) {
                    //特殊处理一下
                    case 0:
                        filedStr = "";
                        for (int j = 0; j < urls.nodes().size(); j++) {
                            if (!urls.nodes().get(j).toString().equals("任意")) {
                                filedStr += " " + urls.nodes().get(j).toString();
                            }
                        }
                        cloudDataCollect.setSearch_area(filedStr.trim());
                        break;
                    case 1:
                        cloudDataCollect.setSearch_resource(filedStr);
                        break;
                    case 2:
                        cloudDataCollect.setSearch_method(filedStr);
                        break;
                    case 3:
                        cloudDataCollect.setSearch_type(filedStr);
                        break;
                    case 4:
                        cloudDataCollect.setSearch_rent(filedStr);
                        break;
                    default:
                        System.out.println("null>>");

                }
            }
        }*/
        //处理更多条件
        st = page.getHtml().xpath("/html/body/div[3]/form/div/div[2]/div[2]/div[2]/div");
        List<Selectable> list4MoreCondition = st.nodes();
        if (list4MoreCondition.size() == 4) {
            cloudDataCollect.setSearch_house_type(this.checkStr4MoreCondition(list4MoreCondition.get(0).xpath("label/text()").toString()));
            cloudDataCollect.setSearch_floor_cout(this.checkStr4MoreCondition(list4MoreCondition.get(1).xpath("label/text()").toString()));
            cloudDataCollect.setSearch_decorate(this.checkStr4MoreCondition(list4MoreCondition.get(2).xpath("label/text()").toString()));
        }
        System.out.println(cloudDataCollect);
    }

    public FirstParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    public FirstParseData() {
    }

    //小工具==>更多条件
    private String checkStr4MoreCondition(String inputStr) {
        String patternStr = "房型楼层装修";
        if (patternStr.contains(inputStr)) {
            return null;
        }
        return inputStr;
    }

    //小工具==>house main info
    private void complete4HouseMainInfo(Page page, Selectable selectable) {
        List<Selectable> selectables = selectable.nodes();
        for (int i = 0; i < selectables.size(); i++) {
            Selectable eachSel = selectables.get(i);
            //price
            if ("价格：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("price", eachSel.xpath("dd/div/span/text()").toString() + eachSel.xpath("dd/div/text()").toString().trim());
            }
            //area unit_price
            else if ("面积：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("area", ParseUtil.getContWithReg(eachSel.xpath("dd").toString(), "<ddclass=\"info\">", "<em").replaceAll("[^\\d,\\.]", "") + "㎡");
                page.putField("unit_price", ParseUtil.selfTrim(eachSel.xpath("dd/em/text()").toString()));
            }
            //decorate
            else if ("装修：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("decorate", ParseUtil.selfTrim(eachSel.xpath("dd/text()").toString()));
            }
            //type
            else if ("类型：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("type", ParseUtil.selfTrim(eachSel.xpath("dd/text()").toString())
                        + "" + ParseUtil.selfTrim(eachSel.xpath("dd/a/text()").toString())
                );
            }
            //house_type
            else if ("户型：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("house_type", ParseUtil.selfTrim(eachSel.xpath("dd/text()").toString()));
            }
            //floor
            else if ("楼层：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("floor", ParseUtil.selfTrim(eachSel.xpath("dd/text()").toString()));
            }
            //address
            else if ("区域：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("address", ParseUtil.selfTrim(eachSel.xpath("dd/a[1]/text()").toString())
                        + "/" + ParseUtil.selfTrim(eachSel.xpath("dd/a[2]/text()").toString())
                );
            }
            //物业费
            else if ("物业费：".equals(eachSel.xpath("dt/text()").toString())) {
                page.putField("property_price", ParseUtil.selfTrim(eachSel.xpath("dd/text()").toString()));
            }
            //所属小区
            else if ("小区：".equals(eachSel.xpath("dt/text()").toString())) {

                page.putField("belong_community", ParseUtil.selfTrim(eachSel.xpath("dd/a[1]/text()").toString() == null ? eachSel.xpath("dd/text()").toString() : eachSel.xpath("dd/a[1]/text()").toString()));
            }
            //更新时间
            else if (eachSel.toString() != null && eachSel.toString().contains("更新时间")) {
                page.putField("flush_time", eachSel.toString().replaceAll("/r|/n|<\\/?.+?\\/?>|[\\u4E00-\\u9FA5]", "").trim().replaceAll("：", ""));//抽取文本
            }
            //联系电话
            else if (eachSel.toString() != null && eachSel.toString().contains("电话")) {
                page.putField("telephone", ParseUtil.selfTrim(eachSel.xpath("/div/div[1]/div/div/p[1]/text()").toString()));//先放一下
            }
        }
    }
//小工具=selef
}
