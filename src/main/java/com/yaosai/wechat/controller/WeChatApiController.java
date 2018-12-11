package com.yaosai.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yaosai.wechat.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @Company: WWW.3GOLDEN.COM.CN
 * @ClassName WeChatApiController
 * @Description: 微信接口调用控制器
 * @Params: Test
 * @Author: YaoS
 * @Create: 2018-12-11 15:39
 **/
@Controller
@RequestMapping(value = "/wechat")
public class WeChatApiController {
    @Value("${wechat.getcode}")
    private static String GET_CODE;

    @Value("${wechat.rollback}")
    private static String ROLLBACK;

    @Value("${wechat.appid}")
    private static String APPID;

    @Value("${wechat.scope}")
    private static String SCOPE;

    @Value("${wechat.getopenid}")
    private static String GET_OPENID;

    @Value("${wechat.appsecret}")
    private static String APPSECRET;


    /**
     * 获取Code代码，并根据code跳转回调页面
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @author YaoS
     * @date 17:18 18/12/1
     **/
    @RequestMapping(value = "/getCode.jspx")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        //处理一下授权回调地址，防止微信无法识别
        String redirectUri = URLEncoder.encode(ROLLBACK, "UTF-8");
        //参数response_type与scope与state参数固定写死
        StringBuffer uri = new StringBuffer(GET_CODE
                + "?redirect_uri=" + redirectUri
                + "&appid=" + APPID
                + "&response_type=code"
                + "&scope=" + SCOPE
                + "&state=1#wechat_redirect");
        //这里请不要使用get请求，单纯的将页面跳转到该url
        response.sendRedirect(uri.toString());
    }

    /**
     * 获取OpenID,并根据获取结果进行跳转
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @author YaoS
     * @date 17:21 18/12/1
     **/
    @RequestMapping(value = "/getOpenID.jspx")
    public void getOpenID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //获取code
        String code = request.getParameter("code");
        String url = GET_OPENID + "?appid=" + APPID + "&secret=" + APPSECRET
                + "&code=" + code + "&grant_type=authorization_code";

        String result = HttpClientUtil.get(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        Object json = jsonObject.get("openid");
        //可能返回错误信息，无openid
        //执行路由方法进行跳转
        if (json != null) {
            route(response, json.toString());
        } else {
            response.sendRedirect("https://www.baidu.com/search/error.html");
        }
    }

    /**
     * 路由方法，根据传入的openID进行对应的操作
     *
     * @param response response对象
     * @param openId   微信的openId
     * @return
     * @author YaoS
     * @date 18:49 18/12/1
     **/
    private void route(HttpServletResponse response, String openId) {
        String url = "";
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
