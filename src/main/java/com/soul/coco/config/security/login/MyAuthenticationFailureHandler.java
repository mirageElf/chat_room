package com.soul.coco.config.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soul.coco.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录失败处理
 * @author lh
 * @date 2020/07/17 9:25
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        Result result = new Result();
        // 用户名或者秘密错误，在UserDetailsService中，将异常已经抛过来了
        if (e instanceof InternalAuthenticationServiceException) {
            result.put("code", -2);
            result.put("msg", "内部认证异常");
        } else {
            result.put("code", -1);
            result.put("msg", e.getMessage());
        }
        httpServletResponse.setContentType("applicatoin/json;charset=utf-8");
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(result));  //返回json数据
    }

}
