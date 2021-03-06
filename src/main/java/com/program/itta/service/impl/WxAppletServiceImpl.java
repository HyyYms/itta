package com.program.itta.service.impl;

import com.program.itta.common.config.JwtConfig;
import com.program.itta.common.util.HttpUtil;
import com.program.itta.common.util.JSONUtil;
import com.program.itta.common.util.SslUtil;
import com.program.itta.domain.dto.Code2SessionResponse;
import com.program.itta.domain.dto.Token;
import com.program.itta.domain.dto.UserDTO;
import com.program.itta.domain.entity.User;
import com.program.itta.mapper.WxAccountRepository;
import com.program.itta.service.WxAppletService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
 * @program: itta
 * @description: 微信 行为描述
 * @author: Mr.Huang
 * @create: 2020-04-05 12:09
 **/
@Service
public class WxAppletServiceImpl implements WxAppletService {
    @Resource
    private RestTemplate restTemplate;
    @Value("${wx.applet.appid}")
    private String appid;
    @Value("${wx.applet.appsecret}")
    private String appSecret;
    @Resource
    private WxAccountRepository wxAccountRepository;
    @Resource
    private JwtConfig jwtConfig;
    /**
     * 微信的 code2session 接口 获取微信用户信息
     * 官方说明 : https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/code2Session.html
     * @return
     */
    private String code2Session(String jsCode) {
        CloseableHttpResponse response = null;
        String resultString = "";
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        //String code2SessionUrl = "https://www.baidu.com";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("appid", appid);
        params.add("secret", appSecret);
        params.add("js_code", jsCode);
        params.add("grant_type", "authorization_code");
        URI code2Session = HttpUtil.getURIwithParams(code2SessionUrl, params);
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(code2Session);

        // 执行请求
        CloseableHttpClient sslClientDefault = SslUtil.createSSLClientDefault();
        try {
            response = sslClientDefault.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
    /**
     * 微信小程序用户登陆，完整流程可参考下面官方地址，本例中是按此流程开发
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
     * @param code 小程序端 调用 wx.login 获取到的code,用于调用 微信code2session接口
     * @return 返回后端 自定义登陆态 token  基于JWT实现
     */
    @Override
    public Token wxUserLogin(String code) {
        //1 . code2session返回JSON数据
        String resultJson = code2Session(code);
        //2 . 解析数据
        Code2SessionResponse response = JSONUtil.jsonString2Object(resultJson, Code2SessionResponse.class);
        if (!"0".equals(response.getErrcode())) {
            throw new AuthenticationException("code2session失败 : " + response.getErrmsg());
        } else {
            //3 . 先从本地数据库中查找用户是否存在
            User wxAccount = wxAccountRepository.findByWxOpenid(response.getOpenid());
            String markId = UUID.randomUUID().toString();
            if (wxAccount == null) {
                wxAccount = new User();
                wxAccount.setWxOpenid(response.getOpenid());    //不存在就新建用户
                wxAccount.setMarkId(markId);
            }
            //4 . 更新sessionKey和 登陆时间
            wxAccount.setSessionKey(response.getSession_key());
            wxAccount.setLastTime(new Date());
            wxAccountRepository.save(wxAccount);
            //5 . JWT 返回自定义登陆态 Token
            String token = jwtConfig.createTokenByWxAccount(wxAccount);
            return new Token(token);
        }
    }
}
