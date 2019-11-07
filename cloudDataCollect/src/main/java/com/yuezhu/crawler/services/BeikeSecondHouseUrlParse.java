package com.yuezhu.crawler.services;

import com.yuezhu.crawler.dao.CloudSecondHouseMapper;
import com.yuezhu.crawler.model.CloudSecondHouse;
import com.yuezhu.util.ParseUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: crawler
 * @description: 贝壳-二手房-url --解析
 * @author: Mr.Chen
 * @create: 2019-09-04 20:11
 **/
@Component("beikeSecondHouseUrlParse")
public class BeikeSecondHouseUrlParse implements ParseData {
    @Resource
    private CloudSecondHouseMapper mapper;
    @Override
    public void preProcess4Request(Page page) {
        Request request;
        String url = "";
        int pageTotal=0;//总页数
        int curPageNum =Integer.valueOf(page.getRequest().getHeaders().get("curPageNum"));
        page.setSkip(true);
        //计算出当前采集的总页数
        if(curPageNum==1){
            String str=page.getHtml().xpath("//div[@class='page-box house-lst-page-box']/@page-data").toString();
            str= ParseUtil.getContWithReg(str,":",",");
            pageTotal=Integer.valueOf(str);
        }
        //处理地址列表
        List<String> links= page.getHtml().xpath("//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[4]/ul/li[@class='clear']/a").links().all();
        //将结果集放入数据库中
        if(CollectionUtils.isNotEmpty(links)) {
            //校验当前页数是否采集过
            List<CloudSecondHouse> models=new ArrayList<>();
            for (int i = 0; i < links.size(); i++) {
                String s =  links.get(i);
                CloudSecondHouse param=new CloudSecondHouse();
                param.setCollectFlag("0");
                param.setWebUrl(s);
                List<CloudSecondHouse> list4veris=this.mapper.selectByObj(param);
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

    @Override
    public void dealwithSearchCondition(Page page) {

    }
    //default....
    @Override
    public Request[] getStartRequests() {
        String baseUrl="https://nj.ke.com/ershoufang/";
        String url="https://nj.ke.com/ershoufang/pg1";
        Request request=new Request(url);
        request.addHeader("baseUrl",baseUrl);//传参用
        request.addHeader("curPageNum","1");//传参用
        return new Request[]{request};
    }

    /**
     * @Description: 备用...
     * @Param:
     * @Return:
     * @Author: Mr.Chen
     * @Date: 2019/9/3 10:48
     */
    @Override
    public Map<String, String> getCookies() {
        return null;
    }
}
