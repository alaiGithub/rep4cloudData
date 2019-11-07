package com.yuezhu.crawler.buss;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyExecute {
    public static void main(String[] args) {
        // CloudSecondHouseUrlBuss   CloudSecondHouseDetailBuss
        new ClassPathXmlApplicationContext("classpath:yuezhu/applicationContext*.xml").getBean(CloudSecondHouseDetailBuss.class)
                .aggregateEachAndRun();
    }
}
