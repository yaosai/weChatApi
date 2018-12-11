package com.yaosai.wechat.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @Company: WWW.3GOLDEN.COM.CN
 * @ClassName HttpClientUtil
 * @Description: Http接口客户端工具类
 * @Params: Test
 * @Author: YaoS
 * @Create: 2017-07-07 15:10
 **/
public class HttpClientUtil {
    private static final String CHARSET = "utf-8";
    private static int TIMEOUT = 40000;
    private static int TWO_HUNDRED = 200;
    private static int THREE_HUNDRED = 300;
    private static String QUE = "?";
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    /**
     * GET 请求
     *
     * @param url URL地址
     * @return String
     * @author YaoS
     * @date 16:28 18/12/11
     **/
    public static String get(String url) throws IOException, ParseException {
        //时钟
        long startTime = System.currentTimeMillis();
        //url 添加reqSeq 请求时间戳
        url = addReqSeq(url);
        //获取 httpClient
        //返回 信息
        String ret = null;
        try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
            //创建 httpGet请求
            final HttpGet httpGet = new HttpGet(url);
            //添加request参数
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT).setSocketTimeout(TIMEOUT)
                    .setConnectTimeout(TIMEOUT).build();
            httpGet.setConfig(config);
            // 创建一个自定义的响应句柄
            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= TWO_HUNDRED && status < THREE_HUNDRED) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, CHARSET) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            //调用接口
            ret = httpClient.execute(httpGet, responseHandler);
            return ret;
        } finally {
            logger.info(String.format("running time %s ms, get url: %s , return : %s",
                    System.currentTimeMillis() - startTime, url, ret));
        }
    }

    /**
     * POST请求
     *
     * @param url  URL地址
     * @param json 入参的JSON字符串
     * @return String
     * @author YaoS
     * @date 16:29 18/12/11
     **/
    public static String post(String url, String json) throws IOException, ParseException {
        return post(url, json, ContentType.APPLICATION_JSON);
    }

    /**
     * POST请求 适用于自定义提交类型
     *
     * @param url         URL地址
     * @param str         入参的请求参数
     * @param contentType 请求类型
     * @return String
     * @author YaoS
     * @date 16:29 18/12/11
     **/
    public static String post(String url, String str, ContentType contentType) throws IOException, ParseException {
        //时钟
        long startTime = System.currentTimeMillis();
        url = addReqSeq(url);
        String ret = null;
        try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
            HttpPost httpPost = new HttpPost(url);

            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT).setSocketTimeout(TIMEOUT)
                    .setConnectTimeout(TIMEOUT).build();
            httpPost.setConfig(config);
            StringEntity entity = new StringEntity(str, contentType);
            httpPost.setEntity(entity);
            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= TWO_HUNDRED && status < THREE_HUNDRED) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, CHARSET) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            ret = httpClient.execute(httpPost, responseHandler);
            return ret;
        } finally {
            logger.info(String.format("running time %s ms, post url: %s ,param : %s, return : %s",
                    System.currentTimeMillis() - startTime, url, str, ret));
        }

    }

    /**
     * 每个请求后面增加 reqSeq时间戳
     *
     * @param url URL地址
     * @return String 加上时间戳的URL
     * @author YaoS
     * @date 16:31 18/12/11
     **/
    private static String addReqSeq(String url) {
        String reqSeq = String.valueOf((new Date()).getTime());
        if (url.indexOf(QUE) > 0) {
            url = url + "&reqSeq=" + reqSeq;
        } else {
            url = url + "?reqSeq=" + reqSeq;
        }
        return url;
    }

}
