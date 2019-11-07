package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * @program: crawler
 * @description: 房天下--新房--url 解析
 * @author: Mr.Chen
 * @create: 2019-09-06 15:41
 **/
@Component("fangNewHouseUrlParse")
public class FangNewHouseUrlParse implements ParseData{
    @Resource
    private CloudNewPremisesMapper mapper;
    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String url = "";
        int pageTotal=0;//总页数
        int curPageNum =Integer.valueOf(page.getRequest().getHeaders().get("curPageNum"));
        page.setSkip(true);
        //计算出当前采集的总页数
        if(curPageNum==1){
            String str=page.getHtml().xpath("/html/body/div[9]/div/ul/li[3]/div[2]/span[2]/text()").toString();
            pageTotal=Integer.valueOf(str.replaceAll("\\D",""));
        }
        //处理地址列表
        List<String> links= page.getHtml().xpath("//*[@id=\"newhouse_loupai_list\"]/ul/li/div/div[@class='nlc_details']/div[1]/div[1]/a").links().all();
        //将结果集放入数据库中
        if(CollectionUtils.isNotEmpty(links)) {
            //校验当前页数是否采集过
            List<CloudNewPremises> models=new ArrayList<>();
            for (int i = 0; i < links.size(); i++) {
                String s =  links.get(i);
                CloudNewPremises param=new CloudNewPremises();
                param.setCollectFlagOne("0");
                param.setUrlUnique(s);
                List<CloudNewPremises> list4veris=this.mapper.selectByObj(param);
                if(CollectionUtils.isEmpty(list4veris)){
                    param.setId(UUID.randomUUID().toString().replaceAll("-",""));//手动维护主键
                    models.add(param);
                }
            }
            //校验通过
            if(CollectionUtils.isNotEmpty(models)){
                this.mapper.insertBatch(models);
            }
        }
        //处理翻页的业务逻辑==只会执行一次
        if(pageTotal>1&&curPageNum==1){
            for (int i = 2; i < pageTotal+1; i++) {
                url=page.getRequest().getHeaders().get("baseUrl")+i+"/";
                request = new Request(url);
                request.addHeader("curPageNum", i+"");
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void parseData4Page(Page page) {

    }

    @Override
    public Request[] getStartRequests() {
            String baseUrl="https://nanjing.newhouse.fang.com/house/s/a77-b9";//==>xx/
            String url="https://nanjing.newhouse.fang.com/house/s/a77-b91/";
            Request request=new Request(url);
            request.addHeader("baseUrl",baseUrl);//传参用
            request.addHeader("curPageNum","1");//传参用
            return new Request[]{request};
    }
}
