package com.soul.coco.config.security.service.impl;

import com.soul.coco.config.security.dto.SecurityUser;
import com.soul.coco.model.SysUser;
import com.soul.coco.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author lh
 * @date 2020/07/17 9:27
 */
@Component
public class UserSecurityServiceImpl implements UserDetailsService {

    // 日志打印
    private static Logger logger = LoggerFactory.getLogger(UserSecurityServiceImpl.class);

    @Resource
    private SysUserService sysUserService;

    /**
     * 该方法通过外部请求传递的用户名去查找数据库，判断是否存在用户，返回用户信息，以进行身份验证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("用户名：" + username); //打印用户名
        // 用户信息类, 包含账号和密码信息
        SysUser sysUser = null;
        try {
            //根据传入用户名去数据库查找用户信息, 密码不能直接存在数据库, 要经过加密, 常用加密类为: BCryptPasswordEncoder
            sysUser = sysUserService.findObjectByParams(SysUser.builder().username(username).build());
        } catch (Exception exception) {
            // 登录各种失败采取抛错误交由MyAuthenticationFailureHandlertongyi8处理
            exception.printStackTrace();
            throw new UsernameNotFoundException("用户不存在");
        }
        if (null == sysUser) {
            throw new UsernameNotFoundException("用户不存在");
        } else {
            /**
             * org.springframework.security.core.userdetails.User
             * User第一参数是：用户名
             * 第二个参数是：pssword, 是从数据库查出来的
             * 第三个参数是: 权限
             */
            User user =  null;
            SecurityUser securityUser = null;
            try {
                user = new User(username,
                        sysUser.getPassword(),
                        // 配置用户访问权限
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                securityUser = new SecurityUser(user);
                securityUser.setSysUser(sysUser);
            } catch (InternalAuthenticationServiceException exception) {
                throw exception;  // 在此处，将异常接着往外抛，抛给AuthenticationFailureHandler处理
            }
            return securityUser;
        }
    }

}
