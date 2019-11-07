package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.List;
/**
 * @program: crawler
 * @description: 房天下--新房--首页（核心字段）--解析；当前类的任务优先级较高
 * @author: Mr.Chen
 * @create: 2019-09-06 17:07
 **/
@Component("fangNewHouseOneDetailParse")
public class FangNewHouseOneDetailParse implements ParseData {
    @Resource
    private CloudNewPremisesMapper cloudNewPremisesMapper;

    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }

    @Override
    public void parseData4Page(Page page) {
        Selectable slTemp;
        String strTemp;
        List<Selectable> slTemps;
        Html curHtml = page.getHtml();
        //主键.....
        page.putField("id", page.getRequest().getHeaders().get("referer_id"));
        page.putField("collectFlagOne", "1");
        //url detail
        strTemp=curHtml.xpath("//*[contains(@id,'B03_08')]").links().toString();
        page.putField("urlDetail", strTemp);
        if (StringUtils.isNotBlank(strTemp)) {
            page.putField("collectFlagTwo", "0");
        }
        //url pic
        strTemp = curHtml.xpath("//*[@id=\"xfptxq_B03_16\"]").links().toString();
        if (StringUtils.isNotBlank(strTemp)) {
            page.putField("urlPic", strTemp);//仅仅参考用
            String preUrl=strTemp.substring(0,strTemp.lastIndexOf("/"));
            String sufUrl=strTemp.substring(strTemp.lastIndexOf("/")+1);
            String effectUrl=preUrl+"/list_904_"+sufUrl;
            page.putField("backup3", effectUrl);
           page.putField("collectFlagThree", "0");//图片的url是否采集完的标示
        }
        //url house type
        strTemp = curHtml.xpath("//*[@id=\"xfptxq_B03_10\"]").links().toString();
        page.putField("urlHouseType", strTemp);//可能为null
        if (StringUtils.isNotBlank(strTemp)) {
            page.putField("backup1", "0");//留意下
        }
        //other...begin
        //community_name
        page.putField("communityName", curHtml.xpath("/html/body/div[5]/div[3]/div[2]/div[1]/div[1]/div/h1/strong/text()").toString());
        //community_alias
        page.putField("communityAlias", curHtml.xpath("/html/body/div[5]/div[3]/div[2]/div[1]/div[1]/div/span/@title").toString());
        //community_addrdss
        page.putField("communityAddrdss", curHtml.xpath("//*[@id=\"xfptxq_B04_12\"]/span/@title").toString());
       //city_name
        strTemp=curHtml.xpath("//*[@id=\"xfptxq_B02_06\"]/li[2]/a/text()").toString();
        page.putField("cityName", strTemp.substring(0,strTemp.indexOf("新房")));//留一下
       //district_name
        strTemp=curHtml.xpath("//*[@id=\"xfptxq_B02_06\"]/li[3]/a/text()").toString();
        page.putField("districtName", strTemp.substring(0,strTemp.indexOf("楼盘")));//留一下
        //other...end

    }

    @Override
    public Request[] getStartRequests() {
        Request[] requests = null;
        CloudNewPremises param = new CloudNewPremises();
        param.setCollectFlagOne("0");//待处理任务
        param.setPageSize(500);//分页大小
        List<CloudNewPremises> list = this.cloudNewPremisesMapper.selectByObj(param);
        //构建request对象==》task
        if (CollectionUtils.isNotEmpty(list)) {
            requests = new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudNewPremises eachItem = list.get(i);
                Request request = new Request(eachItem.getUrlUnique());
                request.addHeader("referer_id", eachItem.getId());
                requests[i] = request;
            }
        }
        return requests;
    }
}
