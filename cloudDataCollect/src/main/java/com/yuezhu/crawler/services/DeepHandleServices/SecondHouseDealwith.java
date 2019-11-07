package com.yuezhu.crawler.services.DeepHandleServices;

import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.dao.CloudProfessionFileMapper;
import com.yuezhu.crawler.dao.CloudSecondHouseMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import com.yuezhu.crawler.model.CloudProfessionFile;
import com.yuezhu.crawler.model.CloudSecondHouse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: crawler
 * @description: 对二手房中采集的数据二次处理...
 * @author: Mr.Chen
 * @create: 2019-09-12 17:01
 **/
@Component
public class SecondHouseDealwith{
    @Resource
    private CloudSecondHouseMapper cloudSecondHouseMapper;
    @Resource
    private CloudProfessionFileMapper cloudProfessionFileMapper;
    /**
     * @Description: 对新房数据中图片处理===》一张表迁移到另一张表中
     * @Param:
     * @Return:
     * @Author: Mr.Chen
     * @Date: 2019/9/12 13:46
     */
    public void dealwithNewHousePicTransfer(){
        //1.查询出主表中数据
        CloudSecondHouse cloudSecondHouse=new CloudSecondHouse();
        cloudSecondHouse.setPageSize(3000);
        cloudSecondHouse.setSynReal("0");
        List<CloudSecondHouse> cloudSecondHouses=this.cloudSecondHouseMapper.selectByObj(cloudSecondHouse);
        if(cloudSecondHouses!=null&&cloudSecondHouses.size()>0){
            for (int i = 0; i < cloudSecondHouses.size(); i++) {
                CloudSecondHouse secondHouse =  cloudSecondHouses.get(i);
                String pics=secondHouse.getPics();
                if(StringUtils.isNotBlank(pics)){
                    String[] arrPic=pics.split(";");
                    if(ArrayUtils.isNotEmpty(arrPic)){
                        List<CloudProfessionFile> cloudProfessionFiles=new ArrayList<>();
                        for (int j = 0; j < arrPic.length; j++) {
                            CloudProfessionFile item=new CloudProfessionFile();
                            String s = arrPic[j];
                            item.setReferId(secondHouse.getId());
                            item.setFileUrl(s);
                            item.setFileType("0");//无用
                            item.setOneClass((byte)1);//二手房
                            cloudProfessionFiles.add(item);
                        }
                        this.cloudProfessionFileMapper.insertBatch(cloudProfessionFiles);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        // CloudSecondHouseUrlBuss   CloudSecondHouseDetailBuss
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(SecondHouseDealwith.class)
                .dealwithNewHousePicTransfer();
    }
}
