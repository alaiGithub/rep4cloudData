package com.yuezhu.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import sun.misc.BASE64Decoder;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
public class ParseUtil {
public static  WebDriver driver = null;
static {
    //1.chrome
   /* System.setProperty("webdriver.chrome.driver", "C:/Users/Administrator/AppData/Local/Google/Chrome/Application/chromedriver.exe");
    ChromeOptions chromeOptions=new ChromeOptions();
    chromeOptions.addArguments("--start-maximized");
    driver = new ChromeDriver(chromeOptions);*/
    //2.phantomJs
    //设置必要参数
    /*DesiredCapabilities dcaps = new DesiredCapabilities();
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
 driver = new PhantomJSDriver(dcaps);*/

}
    public static String getContent(Selectable context, String pref, String suf) {
        String cnt = context.regex(pref + "." + suf).toString();
        if (!StringUtil.isBlank(cnt)) {
            cnt = cnt.substring(pref.length(), cnt.length() - suf.length());
            //System.out.println(pref + "|" +cnt);
            return cnt;
        }
        return null;
    }

    //对字符串解析 src url ....
    public static String getUrlStr(String sourceStr) {
        String pics = "";
        Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(sourceStr == null ? "" : sourceStr);
        if (m.find()) {
            pics = m.group(1);
        }
        System.out.println(pics);
        return pics;
    }

    //对字符串解析 src url ....
    public static String getHrefStr(String sourceStr) {
        String pics = "";
        Matcher m = Pattern.compile("href\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(sourceStr == null ? "" : sourceStr);
        if (m.find()) {
            pics = m.group(1);
        }
        System.out.println(pics);
        return pics;
    }

    //根据正则对字符串截取
    public static String getContWithReg(String str, String pref, String sufix) {
        if (str == null) {
            str = "";
        }
        String sourceStr = str.replaceAll("\r|\n|\\s|&nbsp;", "");
        String pics = "";
        Matcher m = Pattern.compile(pref + "(.*?)" + sufix).matcher(sourceStr == null ? "" : sourceStr);
        if (m.find()) {
            pics = m.group(1);
        }
//		System.out.println(pics);
        return pics;
    }

    //构建一个http>>get 模拟请求
    public static String getHttpContentByUrl(String url) {

        CloseableHttpClient httpClient = HttpClients.custom().build();  // 创建httpclient对象

        HttpGet request = new HttpGet(url); // 构建htttp get请求
        request.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000).build();
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println(EntityUtils.toString(response.getEntity()));
            return EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //用jsoup根据url和标签名称获取document中的html片段
    public static String getHtmlByUrlAndTagName(String url, String tagName) {
        try {
            Document doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
//					.timeout(3000)
                    .post();
            if (doc.hasText()) {
                Element element = doc.getElementsByTag(tagName).first();
                return element.childNodes().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //用jsoup根据url 获取Document
    public static Document getHtmlByUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
                    .cookie("id58", "xxx")
//                    .cookies(getCookiesFromStrs(""))
//					.timeout(3000)
                    .post();
            if (doc.hasText()) {
                return doc;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //小工具Self_trim 避免为null
    public static String selfTrim(String inputStr) {
        return inputStr == null ? "" : inputStr.trim();
    }

    //小工具exp获取取html中文本 且去掉首尾空格
    public static String getTextFromHtml(String inputStr) {
        if (inputStr == null) {
            return "";
        } else {
            return inputStr.replaceAll("<\\/?.+?\\/?>", "").trim().replaceAll("&nbsp;","");
        }
    }

    //小工具 判断字符串是否 被包含在目标字符串中
    public static boolean checkIsContained(String sourceStr, String targetStr) {
        if (!StringUtil.isBlank(targetStr) && targetStr.contains(sourceStr)) {
            return true;
        }
        return false;
    }

    //小工具 格式化时间 对于35分钟前，20小时前，或者04-03 ...==>统一变为2019-xx-xx
    public static String formateTimeNotRegul(String str) {
        String sourStr = str.replaceAll("&nbsp;", "");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter4Medium = DateTimeFormatter.ofPattern("yyyy");
        if (!StringUtil.isBlank(sourStr) && sourStr.contains("分钟前")) {
            long num = Long.valueOf(sourStr.replaceAll("[\\D]", ""));
            LocalDateTime cur = now.minusMinutes(num);
            return cur.format(dateTimeFormatter);
        } else if (!StringUtil.isBlank(sourStr) && sourStr.contains("小时前")) {
            long hour = Long.valueOf(sourStr.replaceAll("[\\D]", ""));
            return dateTimeFormatter.format(now.minusHours(hour));
        } else if (!StringUtil.isBlank(sourStr) && sourStr.contains("天前")) {
            long day = Long.valueOf(sourStr.replaceAll("[\\D]", ""));
            return dateTimeFormatter.format(now.minusDays(day));
        } else if (!StringUtil.isBlank(sourStr) && sourStr.split("-").length == 2) {
            return formatter4Medium.format(now) + "-" + sourStr + " 00:00:00";
        }
        return null;
    }

    public static Map<String, String> getCookiesFromStrs(String string) {
        String inputStr = "userid360_xml=12FFEDF98DF40B5AC0BF84DE0CBB951A; time_create=1557541265296; f=n; commontopbar_new_city_info=172%7C%E5%8D%97%E4%BA%AC%7Cnj; commontopbar_ipcity=nj%7C%E5%8D%97%E4%BA%AC%7C0; id58=c5/njVytaocd/VO1A1QzAg==; 58tj_uuid=6395ca49-9629-43b7-a0a0-cb02c77a27c2; als=0; wmda_uuid=4aa39888ffd846ac53b746e003173a34; wmda_new_uuid=1; xxzl_deviceid=lo6oxzf4%2BteC0ZEdyfdWjsNIl6q8NqVJMdCWuzVcVC3UBFqlCvrT7A8MNLmjmOMo; mcity=nj; wmda_visited_projects=%3B6333604277682%3B2385390625025; city=nj; __utma=253535702.108068999.1554948945.1554948945.1554948945.1; __utmz=253535702.1554948945.1.1.utmcsr=nj.58.com|utmccn=(referral)|utmcmd=referral|utmcct=/qiuzu/0/; 58home=nj; defraudName=defraud; ppStore_fingerprint=AB2CE04AEAF262FB01307AE834FD074229198311868818EA%EF%BC%BF1555466703594; JSESSIONID=75E879B384CE9F2CFED4B8099EF8C3CA; wmda_session_id_6333604277682=1555481209158-1a6d79bf-110e-bad3; new_session=1; new_uv=15; utm_source=; spm=; init_refer=; f=n; xzfzqtoken=KKa6Q2%2BSLUZ8V5PZEZPRB8K6Be2Pa2KY5OM95iKf9RklII2l8oWEaPn%2Bsq5Dn4%2BKin35brBb%2F%2FeSODvMgkQULA%3D%3D";
        Map<String, String> map = new HashMap<>();
        String[] jzdStr = inputStr.split(";");
        for (int i = 0; i < jzdStr.length; i++) {
            String s = jzdStr[i].trim();
            String[] entryStr = s.split("=");
            if (entryStr.length == 2)
                map.put(entryStr[0], entryStr[1]);
        }
        return map;
    }

    //从jsonStr中获取json 且将我们需要的所有属性收集起来
    public static String getFieldsStrFromJsonStr(String jsonStr) {
        StringBuffer sb = new StringBuffer();
        JSONArray arr = JSON.parseArray(jsonStr);
        Iterator iterator = arr.iterator();
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            sb.append(";" + next.getString("src"));
        }
        return sb.toString();
    }


    public static void byteToFile(byte[] contents, String filePath) {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream output = null;
        try {
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(contents);
            bis = new BufferedInputStream(byteInputStream);
            File file = new File(filePath);
            // 获取文件的父路径字符串
            File path = file.getParentFile();
            if (!path.exists()) {
                System.out.println("文件夹不存在，创建。path={}" + path);
                boolean isCreated = path.mkdirs();
                if (!isCreated) {
                    System.out.println("创建文件夹失败，path={}" + path);
                }
            }
            fos = new FileOutputStream(file);
            // 实例化OutputString 对象
            output = new BufferedOutputStream(fos);
            byte[] buffer = new byte[1024];
            int length = bis.read(buffer);
            while (length != -1) {
                output.write(buffer, 0, length);
                length = bis.read(buffer);
            }
            output.flush();
        } catch (Exception e) {
            System.out.println("输出文件流时抛异常，filePath={}" + e.getMessage());
        } finally {
            try {
                bis.close();
                fos.close();
                output.close();
            } catch (IOException e0) {
                System.out.println("文件处理失败，filePath={}");
            }
        }
    }

    //目前仅仅适用于58font解密
    public static Map<String, Integer> getMapperByBase64Str(String fontBase64) {
        Map<String, Integer> retMapper = new HashMap<>();
//        fontBase64 = "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";
        BASE64Decoder decoder = new BASE64Decoder();
        TTFParser parser = new TTFParser();
        try {
            byte[] bytes = decoder.decodeBuffer(fontBase64);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            TrueTypeFont ttf = parser.parse(inputStream);
            if (ttf != null && ttf.getCmap() != null && ttf.getCmap().getCmaps() != null
                    && ttf.getCmap().getCmaps().length > 0) {
                CmapSubtable[] tables = ttf.getCmap().getCmaps();
                CmapSubtable table = tables[0];// No matter what
                for (int i = 1; i <= 10; i++) {
                    retMapper.putIfAbsent(String.valueOf((char) (int) (table.getCharacterCode(i))), table.getGlyphId(table.getCharacterCode(i)) - 1);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
     /*   Set<Map.Entry<String,Integer>>  iterator=retMapper.entrySet();
        iterator.stream().forEach(System.out::println);*/
        return retMapper;
    }

    //批量替换
    public static String batchReplace(String intputStr,String base64Str) {
        //鑶麣驋万(单价餼鑶麣龤龥元/㎡)
        Map<String,Integer> mapper=getMapperByBase64Str(base64Str);
        Set<String> keySet=mapper.keySet();
        StringBuffer retStr = new StringBuffer();
        List<String> arrStr=new ArrayList<>();
        char[] sequences = intputStr.toCharArray();
        for (int i = 0; i < sequences.length; i++) {
            char sequence = sequences[i];
            arrStr.add(sequence+"");
        }
      Stream<String>  stream4Str=arrStr.stream();
        stream4Str.map((s) -> {
            if (keySet.contains(s)){
                s= String.valueOf(mapper.get(s));
            }
            return s;
        }).forEach(retStr::append);
        return retStr.toString();
    }
    /**
    * @Description: 小工具==》多个属性用分割符，且最后用一个字段存储--配套设施，图片等
            * @Param:
            * @Return:
            * @Author: Mr.Chen
            * @Date: 2019/8/31 11:01
            */
    public static void mulFieldsToCombine(Page page,String fieldName,String position,String splitter){
     Selectable   selectable = page.getHtml().xpath(position);
        List<Selectable> list = selectable.nodes();
        String fieldVal = "";
        for (int i = 0; i < list.size(); i++) {
            Selectable item = list.get(i);
            if (i > 0) {
                fieldVal += splitter + item.toString().replaceAll("\\s|&nbsp;","");
            } else {
                fieldVal += item.toString().replaceAll("\\s|&nbsp;","");
            }
        }
        page.putField(fieldName, fieldVal);
    }
    public static void mulFieldsToCombine(Page page, Selectable selectable, String fieldName, String splitter){
        List<Selectable> list = selectable.nodes();
        String fieldVal = "";
        for (int i = 0; i < list.size(); i++) {
            Selectable item = list.get(i);
            if (i > 0) {
                fieldVal += splitter + item.toString().replaceAll("\\s|&nbsp;","");
            } else {
                fieldVal += item.toString().replaceAll("\\s|&nbsp;","");
            }
        }
        page.putField(fieldName, fieldVal);
    }
/**
* @Description: 相对通用替换字符串中空白字符
        * @Param:
        * @Return:
        * @Author: Mr.Chen
        * @Date: 2019/9/2 10:39
        */
public static String repBlk(String inputStr){
   if(inputStr==null||"null".equals(inputStr)){
       inputStr="";
   }
   return inputStr.replaceAll("\\s|&nbsp;|\\u00A0","");
}

/**
* @Description: 将jsonstr===>map<String,String>
        * @Param:
        * @Return:
        * @Author: Mr.Chen
        * @Date: 2019/9/18 13:55
        */
public static Map<String,String> getMapFromJsonStr(String jsonStr){
    jsonStr="[\n" +
            "{\n" +
            "    \"domain\": \".haozu.com\",\n" +
            "    \"expirationDate\": 1568786174,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"authorization\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"OGEyZTdhNzMwOWEzNjNkODYyY2RkYjc3NDcwMTk3Y2E6MTU2ODc4NTk0Ng==\",\n" +
            "    \"id\": 1\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".haozu.com\",\n" +
            "    \"expirationDate\": 1568786174,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"authorization_sign\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"2da82c2e7c0220ec940a47110e08160e\",\n" +
            "    \"id\": 2\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".haozu.com\",\n" +
            "    \"expirationDate\": 1568958746.339158,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"citydomain\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"nj\",\n" +
            "    \"id\": 3\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".haozu.com\",\n" +
            "    \"expirationDate\": 1568807546.339624,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"haozu_user\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"%7B%22index%22%3A3%2C%22name%22%3A%22haozu%22%2C%22value%22%3A%22haozu%22%2C%22opt_scope%22%3A2%7D\",\n" +
            "    \"id\": 4\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".haozu.com\",\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"Hm_lpvt_826deb6478895f40cc4a3e9b54b0ba24\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": true,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"1568785947\",\n" +
            "    \"id\": 5\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".haozu.com\",\n" +
            "    \"expirationDate\": 1600321946,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"Hm_lvt_826deb6478895f40cc4a3e9b54b0ba24\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"1568618309,1568628031,1568708105,1568768807\",\n" +
            "    \"id\": 6\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".www.haozu.com\",\n" +
            "    \"expirationDate\": 1584262721.793399,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"1buildView\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"%5B%7B%22viewId%22%3A%2238408%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%223143%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568620754%7D%2C%7B%22viewId%22%3A%2238671%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568621109%7D%2C%7B%22viewId%22%3A%2245959%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%223121%22%2C%22districtId%22%3A%221481%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568621492%7D%2C%7B%22viewId%22%3A%2238352%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%228365%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568708180%7D%2C%7B%22viewId%22%3A%2238352%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%228365%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568708990%7D%2C%7B%22viewId%22%3A%2237930%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%228372%22%2C%22districtId%22%3A%221474%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568710337%7D%2C%7B%22viewId%22%3A%2237930%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%228372%22%2C%22districtId%22%3A%221474%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A1%2C%22category%22%3A1%2C%22viewTime%22%3A1568710722%7D%5D\",\n" +
            "    \"id\": 7\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".www.haozu.com\",\n" +
            "    \"expirationDate\": 1584337946.339505,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"1houseView\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"%5B%7B%22viewId%22%3A%221512937%22%2C%22userId%22%3A0%2C%22circleId%22%3A0%2C%22streetId%22%3A%228398%22%2C%22districtId%22%3A%221481%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568709492%7D%2C%7B%22viewId%22%3A%221069050%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22824%22%2C%22streetId%22%3A%228372%22%2C%22districtId%22%3A%221474%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568710390%7D%2C%7B%22viewId%22%3A%221493575%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22823%22%2C%22streetId%22%3A%228389%22%2C%22districtId%22%3A%221478%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568720819%7D%2C%7B%22viewId%22%3A%221067101%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22846%22%2C%22streetId%22%3A%2295990%22%2C%22districtId%22%3A%221476%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568725019%7D%2C%7B%22viewId%22%3A%221067101%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22846%22%2C%22streetId%22%3A%2295990%22%2C%22districtId%22%3A%221476%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568725222%7D%2C%7B%22viewId%22%3A%221075932%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22823%22%2C%22streetId%22%3A%228389%22%2C%22districtId%22%3A%221478%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568773881%7D%2C%7B%22viewId%22%3A%221506372%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22824%22%2C%22streetId%22%3A%223105%22%2C%22districtId%22%3A%221474%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568778701%7D%2C%7B%22viewId%22%3A%221506372%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22824%22%2C%22streetId%22%3A%223105%22%2C%22districtId%22%3A%221474%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568778789%7D%2C%7B%22viewId%22%3A%221456278%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22823%22%2C%22streetId%22%3A%228389%22%2C%22districtId%22%3A%221478%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568785325%7D%2C%7B%22viewId%22%3A%221506372%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22824%22%2C%22streetId%22%3A%223105%22%2C%22districtId%22%3A%221474%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A1%2C%22viewTime%22%3A1568785946%7D%5D\",\n" +
            "    \"id\": 8\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \".www.haozu.com\",\n" +
            "    \"expirationDate\": 1584174796.26366,\n" +
            "    \"hostOnly\": false,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"3houseView\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"%5B%7B%22viewId%22%3A%221237127%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568620033%7D%2C%7B%22viewId%22%3A%22921173%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568620163%7D%2C%7B%22viewId%22%3A%22921616%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568620192%7D%2C%7B%22viewId%22%3A%221078150%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%223143%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568620590%7D%2C%7B%22viewId%22%3A%221078150%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%223143%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568620730%7D%2C%7B%22viewId%22%3A%22921173%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568620932%7D%2C%7B%22viewId%22%3A%22921173%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568621038%7D%2C%7B%22viewId%22%3A%22921173%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568621736%7D%2C%7B%22viewId%22%3A%221078160%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%223143%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568621945%7D%2C%7B%22viewId%22%3A%22921173%22%2C%22userId%22%3A0%2C%22circleId%22%3A%22851%22%2C%22streetId%22%3A%228362%22%2C%22districtId%22%3A%221472%22%2C%22cityId%22%3A%2265%22%2C%22user_uuid%22%3A%22%22%2C%22type%22%3A2%2C%22category%22%3A3%2C%22viewTime%22%3A1568622795%7D%5D\",\n" +
            "    \"id\": 9\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"expirationDate\": 1602572475,\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"_pk_id.1.0dea\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"6924c0053c51632e.1568617275.10.1568785947.1568785326.\",\n" +
            "    \"id\": 10\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"expirationDate\": 1584553325,\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"_pk_ref.1.0dea\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"%5B%22%22%2C%22%22%2C1568785326%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3D-NpNA_8GUOmBB_MiQL0RWKHJhjjEWDuyhKklq_l-dJm7RFmyxEZXYGshZj_C5WDp%26ck%3D6547.13.47.184.452.256.220.355%26shh%3Dwww.baidu.com%26sht%3Dbaiduhome_pg%26wd%3D%26eqid%3De958fae300000a72000000045d8095fb%22%5D\",\n" +
            "    \"id\": 11\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"expirationDate\": 1568787746,\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"_pk_ses.1.0dea\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"*\",\n" +
            "    \"id\": 12\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"expirationDate\": 1569315521.793338,\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"lookBuilding\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"37930%2C38352%2C45959%2C38671%2C38408\",\n" +
            "    \"id\": 13\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"expirationDate\": 1569390746.339698,\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"lookHouse\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": false,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"1506372%2C1456278%2C1075932%2C1067101%2C1493575%2C1069050%2C1512937%2C1461734%2C1279293%2C1360045%2C1476272%2C1355578\",\n" +
            "    \"id\": 14\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"MEIQIA_TRACK_ID\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": true,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"1QuOzJzC6Wcn46yowIHu9ROtMph\",\n" +
            "    \"id\": 15\n" +
            "},\n" +
            "{\n" +
            "    \"domain\": \"www.haozu.com\",\n" +
            "    \"hostOnly\": true,\n" +
            "    \"httpOnly\": false,\n" +
            "    \"name\": \"MEIQIA_VISIT_ID\",\n" +
            "    \"path\": \"/\",\n" +
            "    \"sameSite\": \"no_restriction\",\n" +
            "    \"secure\": false,\n" +
            "    \"session\": true,\n" +
            "    \"storeId\": \"0\",\n" +
            "    \"value\": \"1QztbxwapMsd9uN5DTdeZ5FHIQs\",\n" +
            "    \"id\": 16\n" +
            "}\n" +
            "]";
    JSONArray jsonArray=JSONArray.parseArray(jsonStr);
    Map<String,String> cookies=new HashMap<>();
    for (int i = 0; i < jsonArray.size(); i++) {
        Object o =  jsonArray.get(i);
        JSONObject jsonObject= (JSONObject) JSON.parse(o.toString());
        cookies.put(jsonObject.getString("name"),jsonObject.getString("value"));
    }
    return cookies;
}
    /**
    * @Description: 正则测试...
            * @Param:
            * @Return:
            * @Author: Mr.Chen
            * @Date: 2019/8/30 11:04
            */
    public static void main(String[] args) {
      /*  String str=UUID.randomUUID().toString().replaceAll("-","");
        System.out.println(str);*/
//        System.out.println(repBlk("类型:"));
        getMapFromJsonStr("");
    }

}
