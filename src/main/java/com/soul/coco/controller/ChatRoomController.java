package com.soul.coco.controller;

import com.soul.coco.common.mvc.BaseController;
import com.soul.coco.common.utils.Result;
import com.soul.coco.common.utils.SysUserUtils;
import com.soul.coco.model.SysUser;
import com.soul.coco.service.ChatRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * websocket聊天室
 * @author lh
 * @date 2020/07/21 00021 18:19
 */
@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController extends BaseController {

    @Autowired
    private ChatRoomService chatRoomService;

    /**
     * 创建聊天室
     * @param params {
     *      roomName    房间号/名
     *      roomPwd     密码（可不设置密码）
     * }
     * @return
     */
    @RequestMapping("/createRoom")
    public Result createRoom(@RequestBody Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return Result.error("参数错误");
        }
        if (StringUtils.isEmpty(params.get("roomName"))) {
            return Result.error("请输入房间号");
        }
        if ("0".equals(params.get("roomName"))) {
            return Result.error("不允许0作为房间号");
        }

        return chatRoomService.createRoom(params);
    }

    /**
     * 加入聊天室
     * @param params {
     *      roomName    房间号/名
     *      roomPwd     密码（无密码则空）
     * }
     * @return
     */
    @RequestMapping("/joinRoom")
    public Result joinRoom(@RequestBody Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return Result.error("参数错误");
        }
        if (StringUtils.isEmpty(params.get("roomName"))) {
            return Result.error("请输入房间号");
        }

        return chatRoomService.joinRoom(params);
    }

}
