package com.yuezhu.util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.concurrent.TimeUnit;

/**
 * @program: crawler
 * @description: chrome---driver工具
 * @author: Mr.Chen
 * @create: 2019-09-18 15:26
 **/
public class ChromeDriverUtil {
    private static  WebDriver driver = null;
    static {
        //1.chrome
    System.setProperty("webdriver.chrome.driver", "C:/Users/Administrator/AppData/Local/Google/Chrome/Application/chromedriver.exe");
    ChromeOptions chromeOptions=new ChromeOptions();
    chromeOptions.addArguments("--start-maximized");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    }
    public static WebDriver getWebDriver() {
        return driver;
    }
}
