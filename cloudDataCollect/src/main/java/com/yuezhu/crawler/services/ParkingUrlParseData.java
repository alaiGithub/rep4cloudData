package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudParkingSpaceMapper;
import com.yuezhu.crawler.dao.CloudRentHouseMapper;
import com.yuezhu.crawler.model.CloudParkingSpace;
import com.yuezhu.crawler.model.CloudRentHouse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: crawler
 * @description: 58--停车位--采集url解析
 * @author: Mr.Chen
 * @create: 2019-09-16 20:17
 **/
@Component("parkingUrlParseData")
public class ParkingUrlParseData implements ParseData{
    @Resource
    private CloudParkingSpaceMapper mapper;
    @Override
    public void preProcess4Request(Page page){
        Request request;
        String url = "";
        int pageTotal=0;//总页数
        int curPageNum =Integer.valueOf(page.getRequest().getHeaders().get("curPageNum"));
        page.setSkip(true);
        //计算出当前采集的总页数
        List<String> pageLabels=page.getHtml().xpath("/html/body/div[5]/div[4]/div[1]/div[2]//span/text()").all();
        if(CollectionUtils.isNotEmpty(pageLabels)){
            if("下一页".equals(pageLabels.get(pageLabels.size()-1))){
                pageTotal=Integer.valueOf(pageLabels.get(pageLabels.size()-2));
            }
            else {
                pageTotal=Integer.valueOf(pageLabels.get(pageLabels.size()-1));
            }
        }
        //处理地址列表
        List<String> links= page.getHtml().xpath("//*[@id=\"house-list-wrap\"]/li/div[1]/a").links().all();
        //将结果集放入数据库中
        if(CollectionUtils.isNotEmpty(links)) {
            //校验当前页数是否采集过
            List<CloudParkingSpace> models=new ArrayList<>();
            for (int i = 0; i < links.size(); i++){
                String s =  links.get(i);
                CloudParkingSpace param=new CloudParkingSpace();
                param.setCollectFlag("0");
                param.setWebUrl(s);
                List<CloudParkingSpace> list4veris=this.mapper.selectByObj(param);
                if(CollectionUtils.isEmpty(list4veris)){
                    param.setId(UUID.randomUUID().toString().replaceAll("-",""));
                    param.setCityName("南京市");//留意动态切换
                    param.setRentSaleFlag("1");//留意切换0，1,2
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
                url=page.getRequest().getUrl()+"pn"+i+"/";
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
        String url="https://nj.58.com/cheku/b5/";   //留意切换  https://nj.58.com/cheku/b1/==》出租  https://nj.58.com/cheku/b5/==》出售    https://nj.58.com/cheku/b3/ =》转让
        Request request=new Request(url);
        request.addHeader("curPageNum","1");//传参用
        return new Request[]{request};
    }
    @Override
    public Map<String, String> getCookies() {
        Map<String,String> map=new HashMap<>();
        map.put("id58","xxx");
        return map;
    }
}
