package com.yuezhu.crawler.customers;
/**
 * @Description：
 * @author：彭海涛
 * @date:2019/5/23 15:13
 */

import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static String proxyUrl = "http://d.jghttp.golangapi.com/getip?num=1&type=1&pro=320000&city=320100&yys=0&port=1&time=1&ts=0&ys=0&cs=0&lb=1&sb=0&pb=45&mr=2&regions=";
    public static List<Proxy> sendGet() {
        List<Proxy> list = new ArrayList<>();
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(proxyUrl);
            // 打开和URL之间的连接
            Thread.sleep(2000);
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line+",";
            }
            if(result.indexOf(",")!=-1){
                result = result.substring(0,result.lastIndexOf(","));
            }
            if(result.indexOf("code")!=-1){
                Thread.sleep(5000);
                sendGet();
            }else{
                if(result.indexOf(",")!=-1){
                    Proxy proxy = new Proxy(result.split(",")[0].split(":")[0],Integer.parseInt(result.split(",")[0].split(":")[1]));
                    Proxy proxy1 = new Proxy(result.split(",")[1].split(":")[0],Integer.parseInt(result.split(",")[1].split(":")[1]));
                    list.add(proxy);
                    list.add(proxy1);
                }else{
                    list.add(new Proxy(result.split(":")[0],Integer.parseInt(result.split(":")[1])));
                }

            }
            System.out.println("sendGet:"+result);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
            return list;
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return list;
    }

    public static MyHttpClientDownloader getMyHttpClientDownloader(){
        MyHttpClientDownloader httpClientDownloader = new MyHttpClientDownloader();
       List<Proxy> list = new ArrayList<>();
        for(int i=0;i<3;i++){
            list.addAll(sendGet());
        }
        httpClientDownloader.setProxyProvider(new SimpleProxyProvider(list));


//        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
//                new Proxy("180.109.241.61",45631),new Proxy("180.109.240.227",4563)
////                new Proxy("49.77.84.86",4566),new Proxy("180.109.38.12",45661),
////                new Proxy("180.109.37.196",4566),new Proxy("49.77.84.45",45661),
////                new Proxy("49.77.42.100",45661),new Proxy("49.77.43.186",45661),
////                new Proxy("49.77.84.45",4566),new Proxy("49.77.84.71",45661)
//        ));
        return httpClientDownloader;
    }
}
