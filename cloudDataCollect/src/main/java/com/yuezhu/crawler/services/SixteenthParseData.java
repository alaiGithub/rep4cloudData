package com.yuezhu.crawler.services;

import com.yuezhu.crawler.dao.CloudDataOfficeMapper;
import com.yuezhu.crawler.model.CloudDataOffice;
import com.yuezhu.crawler.model.CloudDataShop;
import com.yuezhu.crawler.model.CmConfig;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: crawler
 * @description: 58--写字楼租售 url采集
 * @author: Mr.Chen
 * @create: 2019-08-31 17:04
 **/
@Component("sixteenthParseData")
public class SixteenthParseData implements ParseData {
    @Resource
    private CloudDataOfficeMapper cloudDataOfficeMapper;
    private static String rentSaleFlag;
    @Override
    public void preProcess4Request(Page page) {
        if(rentSaleFlag==null) {
            rentSaleFlag=page.getRequest().getHeaders().get("referer_rent_sale_flag");
        }
        Request request;
        String url = "";
        int pageTotal=0;//总页数
        int curPageNum =Integer.valueOf(page.getRequest().getHeaders().get("referer_pageNum"));
        page.setSkip(true);
        //计算出当前采集的总页数
        if(curPageNum==1){
            List<String> pageLabels=page.getHtml().xpath("/html/body/div[5]/div[4]/div[1]/div[1]/a/span/text()").all();
            if(CollectionUtils.isNotEmpty(pageLabels)){
                if("下一页".equals(pageLabels.get(pageLabels.size()-1))){
                    pageTotal=Integer.valueOf(pageLabels.get(pageLabels.size()-2));
                }
                else {
                    pageTotal=Integer.valueOf(pageLabels.get(pageLabels.size()-1));
                }
            }
        }
        //处理地址列表
//        List<String> links= page.getHtml().xpath("/html/body/div[5]/div[4]/div[1]/ul/li/div[2]/h2/a").links().all();
        List<String> links= page.getHtml().xpath("//*[@id=\"house-list-wrap\"]/li[contains(@logr,'j_1_')]/div[1]/a").links().all();
        //将结果集放入数据库中
        if(CollectionUtils.isNotEmpty(links)) {
                List<CloudDataOffice> cloudDataOfficeList = new ArrayList<>();
                for (int i = 0; i < links.size(); i++) {
                    url = links.get(i).replaceAll("\\^desc", "")
                            .replaceAll("\\|", "")
                            .replaceAll("%7C", "");
                    CloudDataOffice cloudDataOffice = new CloudDataOffice();
                    cloudDataOffice.setWebUrl(url);
                    cloudDataOffice.setCollectFlag("0");
                    cloudDataOffice.setRentSaleFlag(rentSaleFlag);
            List<CloudDataOffice> list4verify=this.cloudDataOfficeMapper.selectByObj(cloudDataOffice);
            if(CollectionUtils.isEmpty(list4verify)){
                cloudDataOffice.setId(UUID.randomUUID().toString().replaceAll("-",""));
                cloudDataOffice.setCityName("南京市");//留意切换
                cloudDataOfficeList.add(cloudDataOffice);
            }
                }
                if(CollectionUtils.isNotEmpty(cloudDataOfficeList)) this.cloudDataOfficeMapper.insertBatch(cloudDataOfficeList);
        }
        //处理翻页的业务逻辑
        if(pageTotal>1&&curPageNum==1){
            for (int i = 2; i < pageTotal+1; i++) {
                if(rentSaleFlag.equals("0")){
                    url=page.getRequest().getUrl()+"pn"+i+"/";//此时租房
                }
                else{
                    url=page.getRequest().getUrl()+"pn"+i+"/"+page.getRequest().getHeaders().get("referer_url_suf");//此时售房
                }
                request = new Request(url);
                request.addHeader("referer_pageNum", i+"");
                page.addTargetRequest(request);
            }
        }
    }

    @Override
    public void parseData4Page(Page page) {

    }
    @Override
    public Request[] getStartRequests() {
        Request request=new Request("https://nj.58.com/zhaozu/");
        request.addHeader("referer_pageNum","1");
        request.addHeader("referer_rent_sale_flag","1");//留意切换
        request.addHeader("referer_url_suf","pve_1092_2");
        return new Request[]{request};
    }

    @Override
    public Map<String, String> getCookies() {
        Map<String,String> map=new HashMap<>();
        map.put("id58","xxx");
        return map;
    }
}
