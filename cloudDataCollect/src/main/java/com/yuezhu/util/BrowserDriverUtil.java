package com.yuezhu.util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
/**
 * @program: crawler
 * @description: driver...manager
 * @author: Mr.Chen
 * @create: 2019-09-05 21:38
 **/
public class BrowserDriverUtil {
    private static ThreadLocal<WebDriver> threadLocal=new ThreadLocal<WebDriver>(){
        @Override
        protected WebDriver initialValue() {
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
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
            return  driver;
        }
    };
    public static WebDriver getWebDriver() {
        return threadLocal.get();
    }

    public static void setWebDriver(WebDriver webDriver) {
        threadLocal.set(webDriver);
    }
}
