package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudHouseTypeMapper;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.model.CloudHouseType;
import com.yuezhu.crawler.model.CloudNewPremises;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
/**
 * @program: crawler
 * @description: 房天下--新房--户型--解析...
 * @author: Mr.Chen
 * @create: 2019-09-07 14:49
 **/
@Component("fangNewHouseFourDetailParse")
public class FangNewHouseFourDetailParse implements ParseData {
    @Resource
    private CloudNewPremisesMapper cloudNewPremisesMapper;
    @Resource
    private CloudHouseTypeMapper cloudHouseTypeMapper;
    @Override
    public void preProcess4Request(Page page) {
        this.parseData4Page(page);
    }
    @Override
    public void parseData4Page(Page page){
        //处理表业务
        page.putField("id",page.getRequest().getHeaders().get("referer_id"));
        page.putField("backup1","1");
       //处理字表业务
        this.completeHouseTypeInfo(page);
    }
    @Override
    public Request[] getStartRequests() {
        Request[] requests=null;
        CloudNewPremises param=new CloudNewPremises();
        param.setBackup1("0");//待处理任务
        param.setPageSize(500);//分页大小
        param.setSwitchFlag(1);//url_house_type is not null
        List<CloudNewPremises> list=this.cloudNewPremisesMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudNewPremises eachItem =  list.get(i);
                Request   request = new Request(eachItem.getUrlHouseType());
                request.addHeader("referer_id", eachItem.getId());
                requests[i]=request;
            }
        }
        return requests;
    }
private void completeHouseTypeInfo(Page page){
        String strTemp;
        int intTemp;
        String referId=page.getRequest().getHeaders().get("referer_id");
       List<Selectable> list=page.getHtml().xpath("//*[@id=\"ListModel\"]/li").nodes();
       List<CloudHouseType> cloudHouseTypes=new ArrayList<>();
       if(CollectionUtils.isNotEmpty(list)){
           for (int i = 0; i < list.size(); i++) {
               CloudHouseType item=new CloudHouseType();
               Selectable selectable =  list.get(i);
               //refer_id
               item.setReferId(referId);
               //name
               item.setTypeName(selectable.xpath("a/img/@title").toString());
               //pic
               item.setHouseTypeImg(selectable.xpath("a/img/@src").toString());
               //beding living chicken wash...
               strTemp=selectable.xpath("p[@class='tiaojian']/a/span[@class='fl']/text()").toString();
               if(StringUtils.isNotBlank(strTemp)){
                   if(strTemp.contains("室")){
                       intTemp=strTemp.indexOf("室");
                       item.setBedroomNum(strTemp.substring(intTemp-1,intTemp));
                   }
                   if(strTemp.contains("厅")){
                       intTemp=strTemp.indexOf("厅");
                       item.setLivingroomNum(strTemp.substring(intTemp-1,intTemp));
                   }
                   if(strTemp.contains("厨")){
                       intTemp=strTemp.indexOf("厨");
                       item.setKitchenNum(strTemp.substring(intTemp-1,intTemp));
                   }
                   if(strTemp.contains("卫")){
                       intTemp=strTemp.indexOf("卫");
                       item.setBathroomNum(strTemp.substring(intTemp-1,intTemp));
                   }
               }
               //structure_acreage
               item.setStructureAcreage(selectable.xpath("p[@class='tiaojian']/a/span[@class='fr']/text()").toString());
               //sale_situation
               item.setSaleSituation(selectable.xpath("p[@class='biaoqian']/span/text()").toString());
               cloudHouseTypes.add(item);
           }
       }
       if(CollectionUtils.isNotEmpty(cloudHouseTypes)){
           this.cloudHouseTypeMapper.insertBatch(cloudHouseTypes);
       }
}
}
