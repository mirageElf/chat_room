package com.soul.coco.config.security.filter;

import com.soul.coco.config.security.CusAuthenticationManager;
import com.soul.coco.config.security.login.MyAuthenticationFailureHandler;
import com.soul.coco.config.security.login.MySuccessAuthenticationHandler;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义用户密码校验过滤器
 * @author lh
 * @date 2020/07/17 9:23
 */
@Component
public class AdminAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * @param authenticationManager:             认证管理器
     * @param mySuccessAuthenticationHandler: 认证成功处理
     * @param myAuthenticationFailureHandler: 认证失败处理
     */
    public AdminAuthenticationProcessingFilter(CusAuthenticationManager authenticationManager, MySuccessAuthenticationHandler mySuccessAuthenticationHandler, MyAuthenticationFailureHandler myAuthenticationFailureHandler) {
        super(new AntPathRequestMatcher("/login", "POST")); // 定义前端登录接口的请求地址以及请求方式
        this.setAuthenticationManager(authenticationManager); // 设置身份验证逻辑处理
        this.setAuthenticationSuccessHandler(mySuccessAuthenticationHandler); // 设置验证成功后续处理
        this.setAuthenticationFailureHandler(myAuthenticationFailureHandler); // 设置验证失败后续处理
    }

    /**
     * 该方法不进行身份验证的逻辑处理，而是委托AuthenticationManager完成相关的身份验证流程
     * 将HttpServletRequest包装成Authentication对象和AuthenticationManager进行交互
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String username = httpServletRequest.getParameter("username");
        String password = httpServletRequest.getParameter("password");

        if (username == null) {
            throw new AuthenticationServiceException("用户名不能为空");
        }

        if (password == null) {
            throw new AuthenticationServiceException("密码不能为空");
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(httpServletRequest));
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
