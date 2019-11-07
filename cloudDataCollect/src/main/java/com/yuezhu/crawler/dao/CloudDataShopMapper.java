package com.yuezhu.crawler.dao;

import com.yuezhu.crawler.model.CloudDataShop;

import java.util.List;

public interface CloudDataShopMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(CloudDataShop record);
    int insertSelective(CloudDataShop record);
    CloudDataShop selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(CloudDataShop record);
    int updateByPrimaryKey(CloudDataShop record);
    int insertBatch(List<CloudDataShop> cloudDataShopList);
    List<CloudDataShop> selectByObj(CloudDataShop cloudDataShop4param);
}