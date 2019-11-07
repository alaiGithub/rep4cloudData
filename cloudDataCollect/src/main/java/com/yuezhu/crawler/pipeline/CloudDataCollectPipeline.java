package com.yuezhu.crawler.pipeline;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.yuezhu.crawler.dao.CloudDataCollectDao;
import com.yuezhu.crawler.model.CloudDataCollect;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
@Component("cloudDataCollectPipeline")
@Log4j
public class CloudDataCollectPipeline implements Pipeline {
    @Resource
    private CloudDataCollectDao cloudDataCollectDao;
    @Override
    public void process(ResultItems resultItems, Task task) {
            CloudDataCollect cloudDataCollect =new CloudDataCollect();
            setProperty(cloudDataCollect, CloudDataCollect.class, resultItems);
            try
            {
                cloudDataCollectDao.insert(cloudDataCollect);
            }
            catch (DataAccessException e)
            {
                final Throwable cause = e.getCause();
                if(cause instanceof MySQLIntegrityConstraintViolationException)
                {
                    log.info("repeat cloudDataCollect,[url]:" + resultItems.get("url") + ";[name]:" + resultItems.get("name"));
                }
                else
                {
                    e.printStackTrace();
                }
            }
    }
    private void setProperty(Object ob, Class cls, ResultItems resultItems){
        PropertyDescriptor[] proDescrtptors = BeanUtils.getPropertyDescriptors(cls);
        if (proDescrtptors != null && proDescrtptors.length > 0) {
            for (PropertyDescriptor propDesc : proDescrtptors) {
                Method methodSet = propDesc.getWriteMethod();
                if(null != methodSet){
                    try {
                        methodSet.invoke(ob, (Object)resultItems.get(propDesc.getName()));
                    } catch (Exception e) {
                        if(e instanceof  IllegalArgumentException){
                            continue;
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
