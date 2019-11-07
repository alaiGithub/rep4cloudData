package com.yuezhu.util;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * @program: crawler
 * @description: xx
 * @author: Mr.Chen
 * @create: 2019-09-05 19:53
 **/
public class TestPhantomJsDriver {
    public static PhantomJSDriver getPhantomJSDriver(){
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"D:\\Program\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        return  driver;
    }

    public static void main(String[] args) {
        WebDriver driver=getPhantomJSDriver();
        driver.get("https://nj.ke.com/ershoufang/18120417810100332769.html");
        WebElement webElement = driver.findElement(By.cssSelector("html"));
        String str = webElement.getAttribute("outerHTML");
        System.out.println(str);
    }
}
