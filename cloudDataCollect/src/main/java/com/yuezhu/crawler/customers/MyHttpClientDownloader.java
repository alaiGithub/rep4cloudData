package com.yuezhu.crawler.customers;

/**
 * @Description：
 * @author：彭海涛
 * @date:2019/5/23 14:50
 */
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class MyHttpClientDownloader extends MyAbstractDownloader {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, CloseableHttpClient> httpClients = new HashMap();
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();
    private ProxyProvider proxyProvider;
    private boolean responseHeader = true;



    public MyHttpClientDownloader() {
    }

    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    private CloseableHttpClient getHttpClient(Site site) {
        if(site == null) {
            return this.httpClientGenerator.getClient((Site)null);
        } else {
            String domain = site.getDomain();
            CloseableHttpClient httpClient = null;
                httpClient = (CloseableHttpClient)this.httpClients.get(domain);
            if(httpClient == null) {
                synchronized(this) {
                    if(httpClient == null) {
                        httpClient = this.httpClientGenerator.getClient(site);
                        this.httpClients.put(domain, httpClient);
                    }
                }
            }

            return httpClient;
        }
    }

    @Override
    public Page download(Request request, Task task,AtomicInteger number) {
        if(number==null){
            number = new AtomicInteger(0);
        }
        if(task != null && task.getSite() != null) {
            CloseableHttpResponse httpResponse = null;
            CloseableHttpClient httpClient = this.getHttpClient(task.getSite());
            Proxy proxy = this.proxyProvider != null?this.proxyProvider.getProxy(task):null;
            this.logger.info("download:===url:"+request.getUrl()+" ,  proxy:"+proxy.getHost()+":"+proxy.getPort());
            HttpClientRequestContext requestContext = this.httpUriRequestConverter.convert(request, task.getSite(), proxy);
            Page page = Page.fail();

            Page var9;
            try {
                httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
                if(httpResponse.getStatusLine().toString().indexOf("403")!=-1 ){
                    this.logger.info("request.getUrl():" +request.getUrl()+"=======================================403");
//                    if( httpResponse.getStatusLine().toString().indexOf("404")!=-1){
////                        System.out.println("request.getUrl():"+request.getUrl());
////                        request.setUrl(request.getUrl().substring(0,request.getUrl().lastIndexOf("com")+3));
//                    }
//                    if(httpResponse.getStatusLine().toString().indexOf("400")!=-1){
////                        return new Page();
//                    }
//                    if(httpResponse != null) {
//                        EntityUtils.consumeQuietly(httpResponse.getEntity());
//                    }
                    System.out.println("httpResponse.getStatusLine():====="+httpResponse.getStatusLine().toString());
                    //如果403也只重新下载2次，2次都不行就不要下载了
//                    if(number.get()<1){
//                        List<Proxy> list = HttpRequest.sendGet();
//                        if(list!=null && list.size()>0){
//                            this.proxyProvider = new SimpleProxyProvider(list);
//                            if(this.proxyProvider.getProxy(task)!=null){
//                                number.getAndIncrement();
//                                this.download(request,task,number);
//                            }
//                        }
//                    }
                }
                page = this.handleResponse(request, request.getCharset() != null?request.getCharset():task.getSite().getCharset(), httpResponse, task);
//                if(page.getHtml().xpath("//*[@id='login_vcode']").nodes().size()>0){
//                    this.logger.info("request.getUrl():" +request.getUrl()+ "需要登陆：=======================================");
//                }
//                if(page.getHtml().xpath("//*[@id='yzmCode']").nodes().size()>0){
//                    this.logger.info("request.getUrl():" +request.getUrl()+"需要验证码：=======================================");
////                    if(number.get()<2){
////                        List<Proxy> list = HttpRequest.sendGet();
////                        if(list!=null && list.size()>0){
////                            this.proxyProvider = new SimpleProxyProvider(list);
////                            if(this.proxyProvider.getProxy(task)!=null){
////                                number.getAndIncrement();
////                                this.download(request,task,number);
////                            }
////                        }
////                    }
//                }
//                if(page.getHtml().xpath("//*[@id='login_vcode']").nodes().size()>0){
//                    logger.debug("需要登陆：=======================================");
//                    if(httpResponse != null) {
//                        EntityUtils.consumeQuietly(httpResponse.getEntity());
//                    }
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    this.proxyProvider = new SimpleProxyProvider(HttpRequest.sendGet());
//                    this.download(request,task);
//                } else if(page.getHtml().xpath("//*[@id='yzmCode']").nodes().size()>0){
//                    logger.debug("需要验证码：=======================================");
//                    if(httpResponse != null) {
//                        EntityUtils.consumeQuietly(httpResponse.getEntity());
//                    }
//                    try {
//                        Thread.sleep(7000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    this.proxyProvider = new SimpleProxyProvider(HttpRequest.sendGet());
//                    this.download(request,task);
//
//                }
                this.onSuccess(request);
                this.logger.info("downloading page success {}", request.getUrl());
                Page var8 = page;
                return var8;
            } catch (IOException var13) {
//                if(httpResponse != null) {
//                    EntityUtils.consumeQuietly(httpResponse.getEntity());
//                }
//                try {
//                    Thread.sleep(30000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                String ip =  HttpRequest.sendGet();
//                if(ip.indexOf(",")!=-1){
//                    this.proxyProvider = SimpleProxyProvider.from( new Proxy(ip.split(",")[0].split(":")[0],Integer.parseInt(ip.split(",")[0].split(":")[1])),
//                            new Proxy(ip.split(",")[1].split(":")[0],Integer.parseInt(ip.split(",")[1].split(":")[1])));
//                    this.download(request,task);
//                }else{
//                    this.logger.info("downloading get myProxy error {}", request.getUrl());
//                }

                var9 = page;
            } finally {
                if(httpResponse != null) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                }

                if(this.proxyProvider != null && proxy != null) {
                    this.proxyProvider.returnProxy(proxy, page, task);
                }

            }

            return var9;
        } else {
            throw new NullPointerException("task or site can not be null");
        }
    }

    @Override
    public void setThread(int thread) {
        this.httpClientGenerator.setPoolSize(thread);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        Page page = new Page();
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null?"":httpResponse.getEntity().getContentType().getValue();

        page.setBytes(bytes);
        if(!request.isBinaryContent()) {
            if(charset == null) {
                charset = this.getHtmlCharset(contentType, bytes);
            }

            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }

        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if(this.responseHeader) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if(charset == null) {
            charset = Charset.defaultCharset().name();
            this.logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }

        return charset;
    }

}
