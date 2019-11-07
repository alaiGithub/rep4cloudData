package com.yuezhu.crawler.services.DeepHandleServices;

import com.yuezhu.crawler.dao.CloudProfessionFileMapper;
import com.yuezhu.crawler.dao.CloudRentHouseMapper;
import com.yuezhu.crawler.model.CloudProfessionFile;
import com.yuezhu.crawler.model.CloudRentHouse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: crawler
 * @description: 租房--数据采集--深度处理
 * @author: Mr.Chen
 * @create: 2019-09-13 11:55
 **/
@Component
public class RentalHouseDealwith {
    @Resource
    private CloudRentHouseMapper cloudRentHouseMapper;
    @Resource
    private CloudProfessionFileMapper cloudProfessionFileMapper;
    /**
     * @Description: 对租房数据中图片处理===》一张表迁移到另一张表中
     * @Param:
     * @Return:
     * @Author: Mr.Chen
     * @Date: 2019/9/12 13:46
     */
    public void dealwithNewHousePicTransfer(){
        //1.查询出主表中数据
        CloudRentHouse cloudRentHouse=new CloudRentHouse();
        cloudRentHouse.setPageSize(3000);
        cloudRentHouse.setSynReal("0");
        List<CloudRentHouse> cloudRentHouses=this.cloudRentHouseMapper.selectByObj(cloudRentHouse);
        if(cloudRentHouses!=null&&cloudRentHouses.size()>0){
            for (int i = 0; i < cloudRentHouses.size(); i++) {
                CloudRentHouse item4rent =  cloudRentHouses.get(i);
                String pics=item4rent.getPics();
                if(StringUtils.isNotBlank(pics)){
                    String[] arrPic=pics.split(";");
                    if(ArrayUtils.isNotEmpty(arrPic)){
                        List<CloudProfessionFile> cloudProfessionFiles=new ArrayList<>();
                        for (int j = 0; j < arrPic.length; j++) {
                            CloudProfessionFile item=new CloudProfessionFile();
                            String s = arrPic[j];
                            item.setReferId(item4rent.getId());
                            item.setFileUrl(s);
                            item.setFileType("0");//无用
                            item.setOneClass((byte)3);//租房
                            cloudProfessionFiles.add(item);
                        }
                        this.cloudProfessionFileMapper.insertBatch(cloudProfessionFiles);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(RentalHouseDealwith.class)
                .dealwithNewHousePicTransfer();
    }
}
