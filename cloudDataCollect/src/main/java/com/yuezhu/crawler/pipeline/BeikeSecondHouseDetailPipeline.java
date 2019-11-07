package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudSecondHouseMapper;
import com.yuezhu.crawler.model.CloudSecondHouse;
import com.yuezhu.util.ReflectionUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;

/**
 * @program: crawler
 * @description: 贝壳--二手房--详情--pipeline
 * @author: Mr.Chen
 * @create: 2019-09-05 09:30
 **/
@Component("beikeSecondHouseDetailPipeline")
public class BeikeSecondHouseDetailPipeline implements Pipeline {
    @Resource
    private CloudSecondHouseMapper mapper;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //此时是更新业务...
        CloudSecondHouse model =new CloudSecondHouse();
        ReflectionUtil.setProperty(model, CloudSecondHouse.class, resultItems);
      /*  //对id特殊处理一下
        model.setId(Integer.valueOf(resultItems.get("id")));*/
        this.mapper.updateByPrimaryKeySelective(model);
    }
}
