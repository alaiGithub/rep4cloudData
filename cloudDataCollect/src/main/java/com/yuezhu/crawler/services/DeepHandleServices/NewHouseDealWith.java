package com.yuezhu.crawler.services.DeepHandleServices;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.dao.CloudProfessionFileMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import com.yuezhu.crawler.model.CloudProfessionFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: crawler
 * @description: 新房--相关--数据处理
 * @author: Mr.Chen
 * @create: 2019-09-12 13:44
 **/
@Component("newHouseDealWith")
@Transactional
public class NewHouseDealWith{
    @Resource
    private CloudNewPremisesMapper cloudNewPremisesMapper;
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
        CloudNewPremises cloudNewPremises=new CloudNewPremises();
        cloudNewPremises.setPageSize(1000);
        cloudNewPremises.setSynReal("0");
        List<CloudNewPremises> cloudNewPremisesList=this.cloudNewPremisesMapper.selectByObj(cloudNewPremises);
        if(cloudNewPremisesList!=null&&cloudNewPremisesList.size()>0){
            for (int i = 0; i < cloudNewPremisesList.size(); i++) {
                CloudNewPremises newPremises =  cloudNewPremisesList.get(i);
                String pics=newPremises.getBackup2();
                if(StringUtils.isNotBlank(pics)){
                    String[] arrPic=pics.split(";");
                    if(ArrayUtils.isNotEmpty(arrPic)){
                        List<CloudProfessionFile> cloudProfessionFiles=new ArrayList<>();
                        for (int j = 0; j < arrPic.length; j++) {
                            CloudProfessionFile item=new CloudProfessionFile();
                            String s = arrPic[j];
                            item.setReferId(newPremises.getId());
                            item.setFileUrl(s);
                            item.setFileType("0");//无用
                            item.setOneClass((byte)0);
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
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(NewHouseDealWith.class)
                .dealwithNewHousePicTransfer();
    }
}
