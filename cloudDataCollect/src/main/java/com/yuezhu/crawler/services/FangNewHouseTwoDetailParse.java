package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.List;
import static com.yuezhu.util.ParseUtil.*;
/**
 * @program: crawler
 * @description: 房天下-新房--详情字段采集--解析
 * @author: Mr.Chen
 * @create: 2019-09-06 19:52
 **/
@Component("fangNewHouseTwoDetailParse")
public class FangNewHouseTwoDetailParse implements ParseData {
    @Resource
    private CloudNewPremisesMapper cloudNewPremisesMapper;
    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }

    @Override
    public void parseData4Page(Page page){
        Selectable slTemp;
        String strTemp;
        List<Selectable> slTemps;
        Html curHtml=page.getHtml();
        //主键.....
        page.putField("id",page.getRequest().getHeaders().get("referer_id"));
        page.putField("collectFlagTwo","1");
        //basic ..info
        slTemp=curHtml.xpath("/html/body/div[5]/div/div[1]/div[1]/ul/li");
        this.completeBasicInfo(page,slTemp);
        //sale...info
        slTemp=curHtml.xpath("/html/body/div[5]/div/div[1]/div[2]/ul/li");
        this.completeSaleInfo(page,slTemp);
        //surrond facility
        slTemp=curHtml.xpath("//*[@id=\"Configuration\"]/ul/li");
        this.completeSurrondFacilityInfo(page,slTemp);
        //community program
        slTemp=curHtml.xpath("/html/body/div[5]/div/div[1]/div[4]/ul/li");
        this.completeCommunityProgramInfo(page,slTemp);
        //community des
        page.putField("communityDesc",curHtml.xpath("/html/body/div[5]/div/div[1]/div[6]/p/text()").all().toString());
    }
    @Override
    public Request[] getStartRequests() {
        Request[] requests=null;
        CloudNewPremises param=new CloudNewPremises();
        param.setCollectFlagTwo("0");//待处理任务
        param.setPageSize(500);//分页大小
        List<CloudNewPremises> list=this.cloudNewPremisesMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudNewPremises eachItem =  list.get(i);
                Request   request = new Request(eachItem.getUrlDetail());
                request.addHeader("referer_id", eachItem.getId());
                requests[i]=request;
            }
        }
        return requests;
    }
    private void completeBasicInfo(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                Selectable s =  selectables.get(i);
                if("物业类别：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("propertyCatagory", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
              else if("项目特色：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("projectSpecial", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
              else if("建筑类别：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("structureCategory", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
              else if("装修状况：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("decorationSituation", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
              else if("产权年限：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("equityDes", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
              else if("环线位置：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("loopPosition", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
              else if("开发商：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("developer", getTextFromHtml(s.xpath("div[@class='list-right']").toString()));
                }
            }
        }
    }
    private void completeSaleInfo(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                Selectable s =  selectables.get(i);
                if("销售状态：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("openCeremonyFlag", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
               else if("楼盘优惠：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("premisesFavorable", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
               else if("开盘时间：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("openingCeremonyTime", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
               else if("交房时间：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("deliverHouseTime", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
               else if("售楼地址：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("salePremisesAddress", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
               else if("咨询电话：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("supportHotline", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
               else if("主力户型：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("mainUnit", repBlk(s.xpath("div[@class='list-right']/text()").toString()));
                }
            }
        }
    }
    private void completeSurrondFacilityInfo(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                Selectable s =  selectables.get(i);
                if("交通".equals(s.xpath("li/span/text()").toString())){
                    page.putField("trafficSituation", repBlk(s.xpath("li/text()").all().toString()));
                }
               else if("综合商场".equals(s.xpath("li/span/text()").toString())){
                    page.putField("shopMarket", repBlk(s.xpath("li/text()").toString()));
                }
               else if("医院".equals(s.xpath("li/span/text()").toString())){
                    page.putField("hospital", repBlk(s.xpath("li/text()").toString()));
                }
               else if("银行".equals(s.xpath("li/span/text()").toString())){
                    page.putField("bank", repBlk(s.xpath("li/text()").toString()));
                }
               else if("邮政".equals(s.xpath("li/span/text()").toString())){
                    page.putField("postOffice", repBlk(s.xpath("li/text()").toString()));
                }
               else if("其他".equals(s.xpath("li/span/text()").toString())){
                    page.putField("otherMatching", repBlk(s.xpath("li/text()").toString()));
                }
               else if("幼儿园".equals(s.xpath("li/span/text()").toString())){
                    page.putField("nurserySchool", repBlk(s.xpath("li/text()").toString()));
                }
               else if("中小学".equals(s.xpath("li/span/text()").toString())){
                    page.putField("middlePrimarySchool", repBlk(s.xpath("li/text()").toString()));
                }
               else if("大学".equals(s.xpath("li/span/text()").toString())){
                    page.putField("univercity", repBlk(s.xpath("li/text()").toString()));
                }
               else if("小区内部配套".equals(s.xpath("li/span/text()").toString())){
                    page.putField("communityInnerMatching", repBlk(s.xpath("li/text()").toString()));
                }
            }
        }
    }
    private void completeCommunityProgramInfo(Page page,Selectable selectable){
        List<Selectable> selectables=selectable.nodes();
        if(CollectionUtils.isNotEmpty(selectables)){
            for (int i = 0; i < selectables.size(); i++) {
                Selectable s =  selectables.get(i);
                if("占地面积：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("floorArea", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("建筑面积：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("structureArea", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("容积率：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("volumeRate", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("绿化率：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("greeningRate", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("停车位：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("parkingDesc", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("楼栋总数：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("premisesTotal", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("总户数：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("totalHousehold", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("物业公司：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("propertyCompany", s.xpath("div[@class='list-right']/a/text()").toString());
                }
              else if("物业费：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("propertyCosts", s.xpath("div[@class='list-right']/text()").toString());
                }
              else if("物业费描述：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("propertyDesc", s.xpath("div[@class='list-right-floor']/text()").toString());
                }
              else if("楼层状况：".equals(s.xpath("div[@class='list-left']/text()").toString())){
                    page.putField("floorCondition", s.xpath("div[@class='list-right-floor']/text()").toString());
                }
            }
        }
    }
}
