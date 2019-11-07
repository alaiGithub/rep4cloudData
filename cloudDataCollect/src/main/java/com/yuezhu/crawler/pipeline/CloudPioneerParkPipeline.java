package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudPioneerParkMapper;
import com.yuezhu.crawler.model.CloudPioneerPark;
import com.yuezhu.util.ReflectionUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;
/**
 * @program: crawler
 * @description: 创业园---数据持久化
 * @author: Mr.Chen
 * @create: 2019-09-18 09:19
 **/
@Component("cloudPioneerParkPipeline")
public class CloudPioneerParkPipeline implements Pipeline {
    @Resource
    private CloudPioneerParkMapper mapper;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //此时是更新业务...
        CloudPioneerPark model =new CloudPioneerPark();
        ReflectionUtil.setProperty(model, CloudPioneerPark.class, resultItems);
        this.mapper.updateByPrimaryKeySelective(model);
    }
}
