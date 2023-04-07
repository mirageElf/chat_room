package com.soul.coco.common.utils;

import com.soul.coco.config.security.dto.SecurityUser;
import com.soul.coco.model.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;


/**
 * 系统用户工具类
 * @author lh
 * @date 2020/07/21 00021 17:28
 */
public class SysUserUtils {

    /**
     * 返回当前登录用户详细信息
     * @return
     */
    public static SysUser getUser() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        SysUser sysUser = securityUser.getSysUser();
        if (sysUser != null) {
            return sysUser;
        }
        return null;
    }

    /**
     * 根据session获取当前登录用户详细信息
     * 用于不同线程获取不到用户信息的情况下使用
     * @param session
     * @return
     * @throws Exception
     */
    public static SysUser getUser(HttpSession session) throws Exception {
        SecurityContext context_session = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        Authentication authentication = context_session.getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        SysUser sysUser = securityUser.getSysUser();
        if (sysUser != null) {
            return sysUser;
        }
        return null;
    }

}
