package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudParkingSpaceMapper;
import com.yuezhu.crawler.model.CloudParkingSpace;
import com.yuezhu.util.ReflectionUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;
/**
 * @program: crawler
 * @description: 停车位--持久化--管理
 * @author: Mr.Chen
 * @create: 2019-09-17 09:15
 **/
@Component("cloudParkingSpacePipeline")
public class CloudParkingSpacePipeline implements Pipeline {
    @Resource
    private CloudParkingSpaceMapper mapper;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //此时是更新业务...
        CloudParkingSpace model =new CloudParkingSpace();
        ReflectionUtil.setProperty(model, CloudParkingSpace.class, resultItems);
        this.mapper.updateByPrimaryKeySelective(model);
    }
}
