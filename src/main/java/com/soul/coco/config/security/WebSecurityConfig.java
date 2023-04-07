package com.soul.coco.config.security;

import com.soul.coco.config.security.filter.AdminAuthenticationProcessingFilter;
import com.soul.coco.config.security.login.MyAuthenticationFailureHandler;
import com.soul.coco.config.security.login.MySuccessAuthenticationHandler;
import com.soul.coco.config.security.service.impl.UserSecurityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author lh
 * @date 2020/07/17 9:28
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 登录成功执行方法
    @Autowired
    private MySuccessAuthenticationHandler mySuccessAuthenticationHandler;
    // 登录失败执行方法
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Bean
    UserDetailsService detailsService() {
        return new UserSecurityServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置 UserDetailsService 实现类，实现自定义登录校验
        auth.userDetailsService(detailsService()).
                // 配置密码加密规则
                        passwordEncoder(passwordEncoder());
    }

    /**
     * 用户密码校验过滤器
     */
    private final AdminAuthenticationProcessingFilter adminAuthenticationProcessingFilter;

    public WebSecurityConfig(AdminAuthenticationProcessingFilter adminAuthenticationProcessingFilter) {
        this.adminAuthenticationProcessingFilter = adminAuthenticationProcessingFilter;
    }

    /**
     * 权限配置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 设置不限制访问静态资源
                .antMatchers(new String[]{"/element/**"}).permitAll()
                // /api下的请求不需要限制
                .antMatchers("/api/**").permitAll()
                // 所有其余路径必须经过身份验证
                .anyRequest().authenticated()
                .and()
                // 禁用csrf,开启跨域
                .csrf().disable().cors().and()
                // 允许frame options
                .headers().frameOptions().sameOrigin()
                .and()
                .formLogin()
                // 由loginPage指定自定义的登录页面
                .loginPage("/login.html")
                // 登录处理url 对应登录页面中登录操作url
//                .loginProcessingUrl("/authentication/form")
                // 成功的url
//                .defaultSuccessUrl("/home.html")
//                .successHandler(mySuccessAuthenticationHandler)   //成功后的处理
//                .failureHandler(myAuthenticationFailureHandler)   //失败处理
                .permitAll()
                .and()
                .logout()
                .permitAll();

        // 自定义过滤器认证用户名密码
        http.addFilterAt(adminAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
