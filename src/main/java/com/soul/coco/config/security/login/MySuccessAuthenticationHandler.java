package com.soul.coco.config.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soul.coco.common.utils.Result;
import com.soul.coco.config.security.dto.SecurityUser;
import com.soul.coco.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录成功处理
 * @author lh
 * @date 2020/07/17 9:26
 */
@Component
public class MySuccessAuthenticationHandler implements AuthenticationSuccessHandler {

    //该bean是springmvc启动的时候实例化的一个对象，纳入到容器中
    @Autowired
    private ObjectMapper objectMapper;

    //登录成功执行的方法
    //authentication中包含了用户的各种信息，包括UserDetail信息
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        SysUser sysUser = securityUser.getSysUser();
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Result.ok("登录成功").put("nickName", sysUser.getNickName())));  //返回json数据
    }

}
