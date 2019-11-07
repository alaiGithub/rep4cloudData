package com.yuezhu.crawler.services;

import com.yuezhu.crawler.enums.Website;

public class CreateParseData {
    private  ParseData parseData;
    public  ParseData getParseDataByFlag(int flag){
        if(flag== Website.HOUSE365_RENT.getCode()){
            parseData=new FirstParseData("//*[@id=\"JS_listPag\"]/dd[contains(@class,'listItem')]","div/a/@href","html/body/div[3]/form/div/div[3]/div[1]//div[2]/ul//a[contains(@class,'next-page')]");
            return  parseData;
        }
        else if(flag== Website.HOUSE365_SALE.getCode()){
            parseData=new SecondParaseData("//*[@id=\"qy_list_cont\"]/div[contains(@class,'info_list')]","dl/dd[1]/a/@href","//*[@id=\"pagebtngroup\"]/p/");
            return  parseData;
        }
        else if(flag== Website.AN_JU_KE_RENT.getCode()){
            parseData=new ThirdParseData("","//*[@id=\"list-content\"]/div[contains(@class,'zu-itemmod')]/@link","//div[contains(@class,'multi-page')]/a[contains(@class,'aNxt')]/@href");
            return  parseData;
        }
        else if(flag== Website.WU_BA_RENT_HOUSE.getCode()){
            parseData=new FourthParseData("","/html/body//ul/li/div[contains(@class,'img_list')]/a/@href","//*[@id=\"bottom_ad_li\"]/div[2]/a[contains(@class,'next')]/@href");
            return  parseData;
        }
        else if(flag== Website.WU_BA_SP_RENT_SALE.getCode()){
            parseData=new FifthParseData("","/html/body//div[contains(@class,'content-wrap')]/div[1]/ul/li/div[contains(@class,'pic')]/a/@href","/html/body//div[contains(@class,'content-wrap')]/div[1]/div/a[contains(@class,'next')]/@href");
            return  parseData;
        }
        else if(flag== Website.WU_BA_XZL_RENT_SALE.getCode()){
            parseData=new SixthParseData("","/html/body//div[contains(@class,'content-wrap')]/div[1]/ul/li/div[contains(@class,'pic')]/a/@href","/html/body//div[contains(@class,'content-wrap')]/div[1]/div/a[contains(@class,'next')]/@href");
            return  parseData;
        }
        else if(flag== Website.WU_BA_ES_SALE.getCode()){
            parseData=new SeventhParseData("","/html/body//div[contains(@class,'content-wrap')]/div[1]/ul/li/div[contains(@class,'pic')]/a/@href","/html/body//div[contains(@class,'content-wrap')]/div[1]/div/a[contains(@class,'next')]/@href");
            return  parseData;
        }
        else if(flag== Website.FANG_DD_RENT.getCode()){
            parseData=new EighthParseData("","//*[@id=\"root\"]/main/div[3]/div[1]/ul/li/a/@href","//*[@id=\"root\"]/main/div[3]/div[1]/div/div[2]/a");
            return  parseData;
        }
        else if(flag== Website.FANG_TIAN_XIA_RENT_HOUSE.getCode()){
            parseData=new TenthParseData("","//*[@id=\"listBox\"]/div[contains(@class,'houseList')]/dl/dt/a/@href","//*[@id=\"rentid_D10_01\"]/a");
            return  parseData;
        }
        else if(flag== Website.FANG_TIAN_XIA_ES_SALE.getCode()){
            parseData=new EleventhParseData("","/html/body/div[3]/div[1]/div[4]/div[contains(@class,'shop_list shop_list_4')]/dl/dt/a/@href","//*[@id=\"list_D10_15\"]/p/a");
            return  parseData;
        }
        else if(flag== Website.FANG_TIAN_XIA_ZXL_RENT_SALE.getCode()){
            parseData=new TwelfthParseData("","/html/body/div[contains(@class,'wid1000')]/div[contains(@class,'listBox')]/div[contains(@class,'houseList')]/dl/dt/a/@href","//*[@id=\"PageControl1_hlk_next\"]/@href");
            return  parseData;
        }
        else if(flag== Website.FANG_TIAN_XIA_SP_RENT_SALE.getCode()){
            parseData=new ThirteenthParseData("","/html/body/div[contains(@class,'main1200 clearfix')]/div[3]/div[contains(@class,'shop_list')]/dl/dt/a/@href","//*[@id=\"PageControl1_hlk_next\"]/@href");
            return  parseData;
        }
       /* else if(flag== Website.WU_BA_SP_RENT_SALE_SHOP.getCode()){
            parseData=new FourteenthParseData();
            return  parseData;
        }*/

        return  null;
    }

}
