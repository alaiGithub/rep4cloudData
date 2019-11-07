package com.yuezhu.crawler.dao;

import com.yuezhu.crawler.model.CloudDataCollect;
import org.springframework.dao.DataAccessException;

public interface CloudDataCollectDao {
        int insert(CloudDataCollect cloudDataCollect) throws DataAccessException;
}
