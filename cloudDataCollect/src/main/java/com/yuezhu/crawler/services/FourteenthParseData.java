package com.yuezhu.crawler.services;
import com.yuezhu.crawler.dao.CloudDataShopMapper;
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
 * @description: 商铺--租 url采集
 * @author: Mr.Chen
 * @create: 2019-08-29 21:26
 **/
@Component("fourteenthParseData")
public class FourteenthParseData implements ParseData {
    @Resource
    private CloudDataShopMapper cloudDataShopMapper;
    private  static String rentSaleFlag;
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
       List<String> pageLabels=page.getHtml().xpath("/html/body/div[5]/div[5]/div[1]/div[1]/a/span/text()").all();
       if(CollectionUtils.isNotEmpty(pageLabels)){
           if("下一页".equals(pageLabels.get(pageLabels.size()-1))){
               pageTotal=Integer.valueOf(pageLabels.get(pageLabels.size()-2));
           }
           else {
               pageTotal=Integer.valueOf(pageLabels.get(pageLabels.size()-1));
           }
       }
        //处理地址列表
        List<String> links= page.getHtml().xpath("/html/body/div[5]/div[5]/div[1]/ul/li/div[1]/a").links().all();
            //将结果集放入数据库中
            if(CollectionUtils.isNotEmpty(links)) {
                    List<CloudDataShop> cloudDataShopList = new ArrayList<>();
                    for (int i = 0; i < links.size(); i++) {
                        url = links.get(i).replaceAll("\\^desc", "")
                                .replaceAll("\\|", "")
                                .replaceAll("%7C", "");
                        CloudDataShop cloudDataShop = new CloudDataShop();
                        cloudDataShop.setWebUrl(url);
                        cloudDataShop.setCollectFlag("0");
                        cloudDataShop.setRentSaleFlag(rentSaleFlag);
                        //校验当前url是否采集过
                        List<CloudDataShop> list4verify=this.cloudDataShopMapper.selectByObj(cloudDataShop);
                        if(CollectionUtils.isEmpty(list4verify)){
                            cloudDataShop.setId(UUID.randomUUID().toString().replaceAll("-",""));
                            cloudDataShop.setCity("南京市");//留意切换
                            cloudDataShopList.add(cloudDataShop);
                        }
                    }
                    this.cloudDataShopMapper.insertBatch(cloudDataShopList);
            }
            //处理翻页的业务逻辑==>全局只调用一次
        if(pageTotal>1&&curPageNum==1){
            for (int i = 2; i < pageTotal+1; i++) {
                url=page.getRequest().getUrl()+"pn"+i;
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
        Request request=new Request("https://nj.58.com/shangpucz/");//留意切换  https://nj.58.com/shangpucz/  https://nj.58.com/shangpucs/
        request.addHeader("referer_pageNum","1");
        request.addHeader("referer_rent_sale_flag","0");//留意切换0/1
        return new Request[]{request};
    }

    @Override
    public Map<String, String> getCookies() {
        Map<String,String> map=new HashMap<>();
        map.put("id58","xxx");
        return map;
    }
}
