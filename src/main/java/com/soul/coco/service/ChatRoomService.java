package com.soul.coco.service;

import com.soul.coco.common.utils.Result;

import java.util.Map;

/**
 * @author lh
 * @date 2020/07/21 00021 18:26
 */
public interface ChatRoomService {

    /**
     * 创建聊天室
     * @param params
     * @return
     */
    Result createRoom(Map<String, String> params);

    /**
     * 加入聊天室
     * @param params
     * @return
     */
    Result joinRoom(Map<String, String> params);

}
