package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import com.yuezhu.util.ParseUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import javax.annotation.Resource;
import java.util.List;
/**
 * @program: crawler
 * @description: 房天下---新房--图片（效果图）--采集解析...
 * @author: Mr.Chen
 * @create: 2019-09-07 14:11
 **/
@Component("fangNewHouseThreeDetailParse")
public class FangNewHouseThreeDetailParse implements ParseData{
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
        page.putField("collectFlagThree","1");
        //pics...此处特指效果图
       ParseUtil.mulFieldsToCombine(page,"backup2","//*[@id=\"gaoqinglist\"]/li/a/img/@src",";");
    }
    @Override
    public Request[] getStartRequests() {
        Request[] requests=null;
        CloudNewPremises param=new CloudNewPremises();
        param.setCollectFlagThree("0");//待处理任务
        param.setPageSize(500);//分页大小
        List<CloudNewPremises> list=this.cloudNewPremisesMapper.selectByObj(param);
        //构建request对象==》task
        if(CollectionUtils.isNotEmpty(list)){
            requests=new Request[list.size()];
            for (int i = 0; i < list.size(); i++) {
                CloudNewPremises eachItem =  list.get(i);
                Request   request = new Request(eachItem.getBackup3());//留意下
                request.addHeader("referer_id", eachItem.getId());
                requests[i]=request;
            }
        }
        return requests;
    }

}
