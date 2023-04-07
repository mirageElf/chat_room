package com.soul.coco.service.impl;

import com.soul.coco.common.mvc.BaseServiceImpl;
import com.soul.coco.dao.SysUserMapper;
import com.soul.coco.model.SysUser;
import com.soul.coco.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author：LH
 * Date：2020-07-10 09:41:38
 */
@Slf4j
@Service
@Transactional
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements SysUserService {
	@Autowired
    private SysUserMapper sysUserMapper;
}
