package com.yuezhu.crawler.enums;
public enum Website {
    HOUSE365_RENT(1,"house365_租房"),
    HOUSE365_SALE(2,"house365_售房"),
    AN_JU_KE_RENT(3,"anjuke_租房"),
    WU_BA_RENT_HOUSE(4,"58_租房"),//数字乱码
    WU_BA_SP_RENT_SALE(5,"58_商铺_租售"),//厂房，仓库，土地，车位(土地，车位中有部分数据丢失)
    WU_BA_XZL_RENT_SALE(6,"58_写字楼_租售"),
    WU_BA_ES_SALE(7,"58_二手房_售房"),//有部分数据丢失的情况
    FANG_DD_RENT(8,"fangdd_租房"),
    FANG_DD_SALE(9,"fangdd_售房"),//目前没有数据源
    FANG_TIAN_XIA_RENT_HOUSE(10,"fangTianXia_租房"),
    FANG_TIAN_XIA_ES_SALE(11,"fangTianXia_二手房_售房"),//首页和子面编码不一样
    FANG_TIAN_XIA_ZXL_RENT_SALE(12,"fangTianXia_写字楼_售租"),
    FANG_TIAN_XIA_SP_RENT_SALE(13,"fangTianXia_商铺_售租");
//    WU_BA_SP_RENT_SALE_SHOP(14,"58_商铺总结");//商铺...
    private  int code;
    private  String desc;
    Website(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public  static  Website getInstanceByCode(int code){
        for (Website website:
           values()  ) {
            if (website.code==code){
                return  website;
            }
        }
        return  null;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
