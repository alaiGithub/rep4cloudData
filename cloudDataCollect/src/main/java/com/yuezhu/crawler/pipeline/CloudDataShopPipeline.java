package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudDataShopMapper;
import com.yuezhu.crawler.model.CloudDataShop;
import com.yuezhu.util.ParseUtil;
import com.yuezhu.util.ReflectionUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
/**
 * @program: crawler
 * @description: ....
 * @author: Mr.Chen
 * @create: 2019-08-29 20:55
 **/
@Component("cloudDataShopPipeline")
@Log4j
public class CloudDataShopPipeline implements Pipeline {
    @Resource
    private CloudDataShopMapper cloudDataShopMapper;
    /**
    * @Description: 新增业务..
            * @Param:
            * @Return:
            * @Author: Mr.Chen
            * @Date: 2019/8/30 9:44
            */
    @Override
    public void process(ResultItems resultItems, Task task) {
        //此时是更新业务...
        CloudDataShop cloudDataShop =new CloudDataShop();
        ReflectionUtil.setProperty(cloudDataShop, CloudDataShop.class, resultItems);
//        对id特殊处理一下
//        cloudDataShop.setId(Integer.valueOf(resultItems.get("id")));
        cloudDataShopMapper.updateByPrimaryKeySelective(cloudDataShop);
    }

}
