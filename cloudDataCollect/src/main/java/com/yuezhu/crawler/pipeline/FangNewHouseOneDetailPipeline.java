package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudNewPremisesMapper;
import com.yuezhu.crawler.model.CloudNewPremises;
import com.yuezhu.util.ReflectionUtil;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;
/**
 * @program: crawler
 * @description: 房天下--新房--核心字段--持久化
 * @author: Mr.Chen
 * @create: 2019-09-06 17:45
 **/
@Component("fangNewHouseOneDetailPipeline")
public class FangNewHouseOneDetailPipeline implements Pipeline {
    @Resource
    private CloudNewPremisesMapper mapper;
    @Override
    public void process(ResultItems resultItems, Task task) {
            //此时是更新业务...
            CloudNewPremises model =new CloudNewPremises();
            ReflectionUtil.setProperty(model, CloudNewPremises.class, resultItems);
            this.mapper.updateByPrimaryKeySelective(model);
    }
}
