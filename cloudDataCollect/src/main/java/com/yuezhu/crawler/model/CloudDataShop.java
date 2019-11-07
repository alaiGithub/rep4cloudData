package com.yuezhu.crawler.model;

import lombok.Data;

import java.util.Date;
@Data
public class CloudDataShop {
    private String id;
    private String webUrl;
    private String collectFlag;
    private String collectPage;
    private String rentSaleFlag;
    private String catagory;
    private String publishSource;
    private String name;
    private String rentPriceOne;
    private String rentPriceTwo;
    private String rentMethod;
    private String rentBeginDate;
    private String structureAcreage;
    private String shopProperty;
    private String managerSituation;
    private String managerType;
    private String floorSituation;
    private String specification;
    private String passengerFlow;
    private String aboutFee;
    private String detailDesc;
    private String matching;
    private String pics;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.address
     *
     * @mbggenerated
     */
    private String address;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.area_lacation
     *
     * @mbggenerated
     */
    private String areaLacation;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.longitude
     *
     * @mbggenerated
     */
    private String longitude;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.latitude
     *
     * @mbggenerated
     */
    private String latitude;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.publish_man
     *
     * @mbggenerated
     */
    private String publishMan;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.publish_man_company
     *
     * @mbggenerated
     */
    private String publishManCompany;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.publish_man_phone
     *
     * @mbggenerated
     */
    private String publishManPhone;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.sale_price_total
     *
     * @mbggenerated
     */
    private String salePriceTotal;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.sale_price_unit
     *
     * @mbggenerated
     */
    private String salePriceUnit;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.expect_income
     *
     * @mbggenerated
     */
    private String expectIncome;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.add_time
     *
     * @mbggenerated
     */
    private Date addTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cloud_data_shop.update_time
     *
     * @mbggenerated
     */
    private Date updateTime;
    private String city;
    private String cityCode;
    private String province;
    private String provinceCode;
    private String county;
    private String countyCode;
    private String synReal;
    //附加字段
    private int pageSize;//分页...

}