package com.soul.coco.api;

import com.soul.coco.common.utils.Result;
import com.soul.coco.model.SysUser;
import com.soul.coco.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author lh
 * @date 2020/07/23 00023 18:10
 */
@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 添加用户
     * @param params
     * @return
     */
    @RequestMapping("/addUser")
    public Result addUser(@RequestParam Map<String, String> params) {
        try {
            if (null == params || params.isEmpty()) {
                return Result.error("参数错误");
            }
            String username = params.get("username");   // 用户名
            String password = params.get("password");   // 密码
            String nickName = params.get("nickName");   // 昵称
            String phone = params.get("phone");         // 手机号
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)
                    || StringUtils.isEmpty(nickName) || StringUtils.isEmpty(phone)) {
                return Result.error("请正确填写参数");
            }

            // SpringSecurity自带的加密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean flag = sysUserService.save(SysUser.builder().username(username)
                    .password(bCryptPasswordEncoder.encode(password))
                    .nickName(nickName).phone(phone).build());
            if (flag) {
                return Result.ok("添加成功");
            } else {
                return Result.error("添加用户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加用户失败");
        }
    }

}
