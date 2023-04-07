package com.soul.coco.service.impl;

import com.soul.coco.common.utils.RedisUtil;
import com.soul.coco.common.utils.Result;
import com.soul.coco.service.ChatRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lh
 * @date 2020/07/21 00021 18:27
 */
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 创建聊天室
     * @param params
     * @return
     */
    @Override
    public Result createRoom(Map<String, String> params) {
        ReentrantLock reentrantLock = new ReentrantLock();
        String roomName = params.get("roomName");   // 房间号
        String roomPwd = params.get("roomPwd");     // 密码

        try {
            // 判断roomName是否已存在
            boolean flag = redisUtil.hasKey(roomName);
            if (!flag) {
                // 不存在则获取锁，创建roomName
                if (reentrantLock.tryLock()) {
                    redisUtil.setString(roomName, roomPwd);
                    return Result.ok("创建成功");
                } else {
                    // 获取锁失败，则代表有其他线程正在执行创建，等待半秒后取值判断是否存在
                    Thread.sleep(500);  // TODO 半秒是否合适
                    flag = redisUtil.hasKey(roomName);
                    if (flag) {
                        return Result.error("房间已存在");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败");
        }

        return Result.error("房间已存在");
    }

    /**
     * 加入聊天室
     * @param params
     * @return
     */
    @Override
    public Result joinRoom(Map<String, String> params) {
        String roomName = params.get("roomName");   // 房间号
        String roomPwd = params.get("roomPwd");     // 密码

        try {
            // 判断是否已存在
            boolean flag = redisUtil.hasKey(roomName);
            if (flag) {
                // 存在则判断是否需要密码，密码是否正确
                String value = redisUtil.getString(roomName);
                if (StringUtils.isEmpty(value)) {
                    return Result.ok();
                } else {
                    if (value.equals(roomPwd)) {
                        return Result.ok();
                    } else {
                        return Result.error("密码错误");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("加入失败");
        }

        return Result.error("房间不存在");
    }

}
