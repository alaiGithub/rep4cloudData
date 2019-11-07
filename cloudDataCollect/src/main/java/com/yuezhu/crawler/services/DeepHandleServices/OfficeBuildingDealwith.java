package com.yuezhu.crawler.services.DeepHandleServices;
import com.yuezhu.crawler.dao.CloudDataOfficeMapper;
import com.yuezhu.crawler.dao.CloudProfessionFileMapper;
import com.yuezhu.crawler.model.CloudDataOffice;
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
 * @description: 写字楼数据采集---深度处理
 * @author: Mr.Chen
 * @create: 2019-09-13 17:00
 **/
@Component
public class OfficeBuildingDealwith {
    @Resource
    private CloudDataOfficeMapper cloudDataOfficeMapper;
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
        CloudDataOffice cloudDataOffice=new CloudDataOffice();
        cloudDataOffice.setPageSize(3000);
        cloudDataOffice.setSynReal("0");
        List<CloudDataOffice> cloudDataOffices=this.cloudDataOfficeMapper.selectByObj(cloudDataOffice);
        if(cloudDataOffices!=null&&cloudDataOffices.size()>0){
            for (int i = 0; i < cloudDataOffices.size(); i++) {
                CloudDataOffice item4office =  cloudDataOffices.get(i);
                String pics=item4office.getPics();
                if(StringUtils.isNotBlank(pics)){
                    String[] arrPic=pics.split(";");
                    if(ArrayUtils.isNotEmpty(arrPic)){
                        List<CloudProfessionFile> cloudProfessionFiles=new ArrayList<>();
                        for (int j = 0; j < arrPic.length; j++) {
                            CloudProfessionFile item=new CloudProfessionFile();
                            String s = arrPic[j];
                            item.setReferId(item4office.getId());
                            item.setFileUrl(s);
                            item.setFileType("0");//无用
                            item.setOneClass((byte)5);//写字楼
                            cloudProfessionFiles.add(item);
                        }
                        this.cloudProfessionFileMapper.insertBatch(cloudProfessionFiles);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(OfficeBuildingDealwith.class)
                .dealwithNewHousePicTransfer();
    }
}
