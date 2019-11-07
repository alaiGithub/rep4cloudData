package com.yuezhu.crawler.dao;

import com.yuezhu.crawler.model.CloudSecondHouse;

import java.util.List;

public interface CloudSecondHouseMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_second_house
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_second_house
     *
     * @mbggenerated
     */
    int insert(CloudSecondHouse record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_second_house
     *
     * @mbggenerated
     */
    int insertSelective(CloudSecondHouse record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_second_house
     *
     * @mbggenerated
     */
    CloudSecondHouse selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_second_house
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(CloudSecondHouse record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_second_house
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(CloudSecondHouse record);

    List<CloudSecondHouse> selectByObj(CloudSecondHouse param);

    void insertBatch(List<CloudSecondHouse> models);
}