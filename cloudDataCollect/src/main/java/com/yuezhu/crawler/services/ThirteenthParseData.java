package com.yuezhu.crawler.services;
import com.yuezhu.crawler.enums.Website;
import com.yuezhu.crawler.model.CloudDataCollect;
import lombok.Data;
import org.jsoup.helper.StringUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;
import java.util.List;
@Data
public class ThirteenthParseData implements ParseData {
    private static final String curFlag = "13";
    private String xpathDivStr;//待维护
    private String xpathUrlStr;//待维护
    private String xpathNextPageStr;//待维护
    private static String domainStr;
    private static CloudDataCollect cloudDataCollect = new CloudDataCollect();//存储搜索条件

    public ThirteenthParseData() {
    }

    public ThirteenthParseData(String xpathDivStr, String xpathUrlStr, String xpathNextPageStr) {
        this.xpathDivStr = xpathDivStr;
        this.xpathUrlStr = xpathUrlStr;
        this.xpathNextPageStr = xpathNextPageStr;
    }

    @Override
    public void preProcess4Request(Page page) {
        String domain = page.getUrl().toString().substring(0, page.getUrl().toString().indexOf(".com") + 5);
        this.domainStr = domain;
        String url;
        Request request;
        String flag = page.getRequest().getHeaders().get("referer");
        //取出当前页中的访问域名
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
            for (Selectable each :
                    st4Urls) {
                url = each.toString();
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
        page.putField("source", Website.FANG_TIAN_XIA_SP_RENT_SALE.getDesc());
        page.putField("search_area", cloudDataCollect.getSearch_area());
        page.putField("search_rent", cloudDataCollect.getSearch_rent());
        page.putField("search_area_size", cloudDataCollect.getSearch_area_size());
        page.putField("search_type", cloudDataCollect.getSearch_type());
        page.putField("search_shop_industry", cloudDataCollect.getSearch_shop_industry());
        page.putField("search_method", cloudDataCollect.getSearch_method());
        /* 处理详情关于的主要的字段 */
        //name
        Selectable temp4Sel;
        String tempStr;
        temp4Sel = page.getHtml().xpath("/html/body/div[3]/div[5]/div[1]/div[3]/h3/text()");
        page.putField("name", temp4Sel.toString());
        //flush_time
        temp4Sel = page.getHtml().xpath("/html/body/div[3]/div[5]/div[1]/div[3]//span[contains(@class,'time')]/text()");
        page.putField("flush_time", temp4Sel.toString().substring(4).replaceAll("/", "-"));
        //address
        tempStr = "";
        temp4Sel = page.getHtml().xpath("//*[@id=\"A3\"]/text()");
        if (!StringUtil.isBlank(temp4Sel.toString())) {
            tempStr += temp4Sel.toString();
        }
        temp4Sel = page.getHtml().xpath("//*[@id=\"A4\"]/text()");
        if (!StringUtil.isBlank(temp4Sel.toString())) {
            tempStr += "/" + temp4Sel.toString();
        }
        page.putField("address", tempStr);
        //concat
        temp4Sel = page.getHtml().xpath("/html/body/div[3]/div[5]/div[1]/div[3]/div[contains(@class,'yztel clearfix')]/div/text()");
        page.putField("concat", temp4Sel.toString());
        //telephone
        temp4Sel = page.getHtml().xpath("/html/body/div[3]/div[5]/div[1]/div[3]/div[contains(@class,'yztel clearfix')]/span/text()");
        page.putField("telephone", temp4Sel.toString());
        //pics
        List<Selectable> sel4Srcs=page.getHtml().xpath("/html/body/div[3]/div[5]/div[2]/div[1]/div[contains(@class,'fang_img')]/div/div/div/img/@src").nodes();
       StringBuffer sb=new StringBuffer();
        for (Selectable eachSrc:
             sel4Srcs) {
            sb.append(";"+eachSrc.toString());
        }
        page.putField("pics",sb.toString());
        //basic_info
        List<Selectable> sel4Lis = page.getHtml().xpath("/html/body/div[3]/div[5]/div[2]/div[1]/div[1]/ul/li").nodes();
        for (Selectable eachLi :
                sel4Lis) {
            if ("租金".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("price", eachLi.xpath("span/text()").toString());
            }
            else if ("总价".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("price", eachLi.xpath("span/text()").toString());
            }
            else if ("建筑面积".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("area", eachLi.xpath("span/text()").toString());
            }
            else if ("所在楼层".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("floor", eachLi.xpath("span/text()").toString());
            }
            else if ("物业费".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("property_price", eachLi.xpath("span/text()").toString());
            }
            else if ("装修".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("decorate", eachLi.xpath("span/text()").toString());
            }
            else if ("类型".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("type", eachLi.xpath("span/text()").toString());
            }
            else if ("是否分割".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_can_split", eachLi.xpath("span/text()").toString());
            }
            else if ("支付方式".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_pay_method", eachLi.xpath("span/text()").toString());
            }
            else if ("适合经营".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_manage_industry", eachLi.xpath("span/text()").toString());
            }
            else if ("楼盘名称".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("bulid_name", eachLi.xpath("span/text()").toString());
            }
            else if ("楼盘地址".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("detail_url", eachLi.xpath("span/text()").toString());
            }
            else if ("面宽".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_face_width", eachLi.xpath("span/text()").toString());
            }
            else if ("进深".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_enter_depth", eachLi.xpath("span/text()").toString());
            }
            else if ("层高".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_layer_height", eachLi.xpath("span/text()").toString());
            }
            else if ("起租期".equals(eachLi.xpath("b/text()").toString().replaceAll("\\s|\u00A0", ""))) {
                page.putField("shop_rent_begTime", eachLi.xpath("span/text()").toString());
            }
        }
    }

    @Override
    public void dealwithSearchCondition(Page page) {
        Selectable temp4Sel;
        String temp4Str;
        List<Selectable> sel4Lis = page.getHtml().xpath("/html/body/div[contains(@class,'main1200 clearfix')]/div[2]/div[2]/ul/li").nodes();
        for (Selectable eachLi :
                sel4Lis) {
            if (!StringUtil.isBlank(eachLi.xpath("span/text()").toString()) && eachLi.xpath("span/text()").toString().contains("区域")) {
                cloudDataCollect.setSearch_area(eachLi.xpath("ul/li[contains(@class,'on')]/a/text()").toString());
            } else if (!StringUtil.isBlank(eachLi.xpath("span/text()").toString()) && eachLi.xpath("span/text()").toString().contains("租金")) {
                temp4Str = "";
                List<Selectable> sel4SubAreas = eachLi.xpath("ul/li").nodes();
                for (Selectable eachItem :
                        sel4SubAreas) {
                    if (!StringUtil.isBlank(eachItem.xpath("label/span[contains(@class,'icon_check  on')]").toString())) {
                        temp4Str += "/" + eachItem.xpath("label/a/text()").toString();
                    }
                }
                cloudDataCollect.setSearch_rent(temp4Str);
            }
            else if (!StringUtil.isBlank(eachLi.xpath("span/text()").toString()) && eachLi.xpath("span/text()").toString().contains("面积")) {
                List<Selectable> sel4SubAreas = eachLi.xpath("ul/li").nodes();
                temp4Str = "";
                for (Selectable eachItem :
                        sel4SubAreas) {
                    if (!StringUtil.isBlank(eachItem.xpath("label/span[contains(@class,'icon_check  on')]").toString())) {
                        temp4Str += "/" + eachItem.xpath("label/a/text()").toString();
                    }
                }
                cloudDataCollect.setSearch_area_size(temp4Str);
            }
            else if (!StringUtil.isBlank(eachLi.xpath("span/text()").toString()) && eachLi.xpath("span/text()").toString().contains("总价")) {
                List<Selectable> sel4SubAreas = eachLi.xpath("ul/li").nodes();
                temp4Str = "";
                for (Selectable eachItem :
                        sel4SubAreas) {
                    if (!StringUtil.isBlank(eachItem.xpath("label/span[contains(@class,'icon_check  on')]").toString())) {
                        temp4Str += "/" + eachItem.xpath("label/a/text()").toString();
                    }
                }
                cloudDataCollect.setSearch_price(temp4Str);
            }
            else {
                temp4Str = "";
                List<Selectable> sel4SubAreas = eachLi.xpath("ul/li").nodes();
                for (Selectable eachItem :
                        sel4SubAreas) {
                    if (!StringUtil.isBlank(eachItem.xpath("span[contains(@class,'icon_check on')]").toString())) {
                        temp4Str += "/" + eachItem.xpath("li/a/text()").toString();
                    }
                }
                cloudDataCollect.setSearch_area(cloudDataCollect.getSearch_area() + temp4Str);
            }
        }
        //more condition
        temp4Sel = page.getHtml().xpath("/html/body/div[contains(@class,'main1200 clearfix')]/div[2]/div[3]/div[1]/p/input/@value");
        if (!StringUtil.isBlank(temp4Sel.toString()) && !temp4Sel.toString().equals("类型")) {
            cloudDataCollect.setSearch_type(temp4Sel.toString());
        }
        temp4Sel = page.getHtml().xpath("/html/body/div[contains(@class,'main1200 clearfix')]/div[2]/div[3]/div[2]/p/input/@value");
        if (!StringUtil.isBlank(temp4Sel.toString()) && !temp4Sel.toString().equals("行业")) {
            cloudDataCollect.setSearch_shop_industry(temp4Sel.toString());
        }
        temp4Sel = page.getHtml().xpath("/html/body/div[contains(@class,'main1200 clearfix')]/div[2]/div[3]/div[3]/p/input/@value");
        if (!StringUtil.isBlank(temp4Sel.toString()) && !temp4Sel.toString().equals("类别")) {
            cloudDataCollect.setSearch_method(temp4Sel.toString());
        }
        System.out.println(cloudDataCollect);
    }
}
