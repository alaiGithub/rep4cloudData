package com.yuezhu.crawler.services.DeepHandleServices;

import com.yuezhu.crawler.dao.CloudParkingSpaceMapper;
import com.yuezhu.crawler.dao.CloudProfessionFileMapper;
import com.yuezhu.crawler.model.CloudParkingSpace;
import com.yuezhu.crawler.model.CloudProfessionFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: crawler
 * @description: 停车位--采集数据--深度处理
 * @author: Mr.Chen
 * @create: 2019-09-17 14:32
 **/
@Component
public class ParkingDealwith {
    @Resource
    private CloudParkingSpaceMapper cloudParkingSpaceMapper;
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
        CloudParkingSpace cloudParkingSpace=new CloudParkingSpace();
        cloudParkingSpace.setPageSize(3000);
        cloudParkingSpace.setSynReal("0");
        List<CloudParkingSpace> cloudParkingSpaces=this.cloudParkingSpaceMapper.selectByObj(cloudParkingSpace);
        if(cloudParkingSpaces!=null&&cloudParkingSpaces.size()>0){
            for (int i = 0; i < cloudParkingSpaces.size(); i++) {
                CloudParkingSpace item4parking =  cloudParkingSpaces.get(i);
                String pics=item4parking.getPics();
                if(StringUtils.isNotBlank(pics)){
                    String[] arrPic=pics.split(";");
                    if(ArrayUtils.isNotEmpty(arrPic)){
                        List<CloudProfessionFile> cloudProfessionFiles=new ArrayList<>();
                        for (int j = 0; j < arrPic.length; j++) {
                            CloudProfessionFile item=new CloudProfessionFile();
                            String s = arrPic[j];
                            item.setReferId(item4parking.getId());
                            item.setFileUrl(s);
                            item.setFileType("0");//无用
                            item.setOneClass((byte)6);//停车位
                            cloudProfessionFiles.add(item);
                        }
                        this.cloudProfessionFileMapper.insertBatch(cloudProfessionFiles);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(ParkingDealwith.class)
                .dealwithNewHousePicTransfer();
    }
}
