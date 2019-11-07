package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudRentHouseMapper;
import com.yuezhu.crawler.model.CloudRentHouse;
import com.yuezhu.util.ReflectionUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;

/**
 * @program: crawler
 * @description: 贝壳-租房-详情 数据持久化...
 * @author: Mr.Chen
 * @create: 2019-09-03 14:07
 **/
@Component("beikeRentalHouseDetailPipeline")
public class BeikeRentalHouseDetailPipeline implements Pipeline {
    @Resource
    private CloudRentHouseMapper mapper;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //此时是更新业务...
        CloudRentHouse model =new CloudRentHouse();
        ReflectionUtil.setProperty(model, CloudRentHouse.class, resultItems);
//        对id特殊处理一下
//        model.setId(Integer.valueOf(resultItems.get("id")));
        this.mapper.updateByPrimaryKeySelective(model);
    }
}
