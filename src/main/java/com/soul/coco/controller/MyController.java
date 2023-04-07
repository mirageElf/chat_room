package com.soul.coco.controller;

import com.soul.coco.service.WebSocketServer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author lh
 * @date 2020/4/16 15:06
 */
@RestController
public class MyController {

    // 推送数据
    /*@RequestMapping("/socket/push/{cid}")
    public String pushToWeb(@PathVariable String cid, String message) {
        try {
            WebSocketServer.sendInfo(message, cid);
        } catch (IOException e) {
            e.printStackTrace();
            return "推送失败";
        }
        return "推送成功";
    }*/
}
