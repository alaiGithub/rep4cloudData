package com.yuezhu.crawler.pipeline;
import com.yuezhu.crawler.dao.CloudDataOfficeMapper;
import com.yuezhu.crawler.model.CloudDataOffice;
import com.yuezhu.util.ReflectionUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import javax.annotation.Resource;

/**
 * @program: crawler
 * @description: ...
 * @author: Mr.Chen
 * @create: 2019-08-31 16:44
 **/
@Transactional
@Component("cloudDataOfficePipeline")
public class CloudDataOfficePipeline implements Pipeline {
    @Resource
    private CloudDataOfficeMapper cloudDataOfficeMapper;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //此时是更新业务...
        CloudDataOffice cloudDataOffice =new CloudDataOffice();
        ReflectionUtil.setProperty(cloudDataOffice, CloudDataOffice.class, resultItems);
        //对id特殊处理一下
//        cloudDataOffice.setId(Integer.valueOf(resultItems.get("id")));
        cloudDataOfficeMapper.updateByPrimaryKeySelective(cloudDataOffice);
    }
}
