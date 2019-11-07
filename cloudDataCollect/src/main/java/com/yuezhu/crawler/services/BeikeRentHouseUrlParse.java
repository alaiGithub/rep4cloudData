package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudRentHouseMapper;
import com.yuezhu.crawler.model.CloudRentHouse;
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
 * @description: 贝壳--租房--url
 * @author: Mr.Chen
 * @create: 2019-09-03 10:47
 **/
@Component("beikeRentHouseUrlParse")
public class BeikeRentHouseUrlParse implements ParseData {
    @Resource
    private CloudRentHouseMapper mapper;
    @Override
    public void preProcess4Request(Page page){
        Request request;
        String url = "";
        int pageTotal=0;//总页数
        int curPageNum =Integer.valueOf(page.getRequest().getHeaders().get("curPageNum"));
        page.setSkip(true);
        //计算出当前采集的总页数
        if(curPageNum==1){
            pageTotal=Integer.valueOf(page.getHtml().xpath("//div[@class='content__pg']/@data-totalpage").toString());
        }
        //处理地址列表
        List<String> links= page.getHtml().xpath("//*[@id=\"content\"]/div[1]/div[1]/div[@class='content__list--item']/a").links().all();
        //将结果集放入数据库中
        if(CollectionUtils.isNotEmpty(links)) {
            //校验当前页数是否采集过
            List<CloudRentHouse> models=new ArrayList<>();
            for (int i = 0; i < links.size(); i++){
                String s =  links.get(i);
                CloudRentHouse param=new CloudRentHouse();
                param.setCollectFlag("0");
                param.setWebUrl(s);
                List<CloudRentHouse> list4veris=this.mapper.selectByObj(param);
                if(CollectionUtils.isEmpty(list4veris)){
                    param.setId(UUID.randomUUID().toString().replaceAll("-",""));
                    param.setCityName("南京市");//留意动态切换
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
                url=page.getRequest().getHeaders().get("baseUrl")+"pg"+i;
                request = new Request(url);
                request.addHeader("curPageNum", i+"");
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void parseData4Page(Page page) {

    }

//default....
    @Override
    public Request[] getStartRequests() {
        String baseUrl="https://nj.zu.ke.com/zufang/";
        String url="https://nj.zu.ke.com/zufang/pg1";
        Request request=new Request(url);
        request.addHeader("baseUrl",baseUrl);//传参用
        request.addHeader("curPageNum","1");//传参用
        return new Request[]{request};
    }
}
