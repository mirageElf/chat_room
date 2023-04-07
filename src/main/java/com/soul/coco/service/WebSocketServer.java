package com.soul.coco.service;

import com.alibaba.fastjson.JSONObject;
import com.soul.coco.common.utils.RedisUtil;
import com.soul.coco.common.utils.SpringUtil;
import com.soul.coco.common.utils.SysUserUtils;
import com.soul.coco.config.websocket.GetHttpSessionConfigurator;
import com.soul.coco.model.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author lh
 * @date 2020/4/16 14:25
 */
@ServerEndpoint(value = "/websocket/{roomName}", configurator = GetHttpSessionConfigurator.class)
@Component
public class WebSocketServer {

    @Autowired
    private RedisUtil redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");

    static Log log = LogFactory.getLog(WebSocketServer.class);
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
    private static int onlineCount = 0;
    // concurrent包的线程安全set，用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet
            = new CopyOnWriteArraySet<WebSocketServer>();
    // 线程安全的map，根据房间放放客户端对应的webSocket对象，map的key为房间号，key为0则代表大厅
    private static ConcurrentHashMap<String, ArrayList<WebSocketServer>> webSocketRoomList = initMap();
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    // 登录用户id
    private String sid = "";
    // 登录用户昵称
    private String name = "";
    // 当前用户所在房间号,默认0(大厅)
    private String currentRoom = "0";

    // 初始化大厅
    private static ConcurrentHashMap<String, ArrayList<WebSocketServer>> initMap() {
        ConcurrentHashMap<String, ArrayList<WebSocketServer>> map = new ConcurrentHashMap<>();
        map.put("0", new ArrayList<>());
        return map;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static synchronized int getOnlineCount() {
        return WebSocketServer.onlineCount;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomName") String roomName, EndpointConfig config) {
        try {
            HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            // 获取当前登录用户
            SysUser sysUser = SysUserUtils.getUser(httpSession);
            Map<String, Object> mapMsg = new HashMap<>();
            if (sysUser == null) {
                // 当前登录用户为空，代表为登录，不进行连接
                log.error("连接失败,用户未登录");
            } else {
                sid = sysUser.getId().toString();
                name = sysUser.getNickName();
                this.session = session;
                // 判断当前是连接大厅还是加入房间
                if (StringUtils.isEmpty(roomName)) {
                    // 将当前用户的websocket对象保存在大厅的集合中
                    webSocketRoomList.get("0").add(this);
                } else {
                    // 加入房间之前先判断房间集合是否存在
                    ArrayList<WebSocketServer> list = webSocketRoomList.get(roomName);
                    if (null == list) {
                        // 不存在则进行添加
                        list = new ArrayList<>();
                        list.add(this);
                        webSocketRoomList.put(roomName, list);
                    } else {
                        webSocketRoomList.get(roomName).add(this);
                    }
                    currentRoom = roomName; // 保存当前所在房间key
                }
                addOnlineCount(); // 在线数+1

                log.info("有新的窗口开始监听：[name=" + name + ", sid=" + sid + "]，当前在线人数为：" + getOnlineCount());

//                mapMsg.put("name", "系统消息");
//                mapMsg.put("msg", "[" + name + "]加入聊天!");
//                mapMsg.put("type", 0);
//                JSONObject jsonObj = new JSONObject(mapMsg);
//                sendMessage(jsonObj.toString());
                sendInfo("[" + name + "]加入聊天!");
            }
        } catch (IOException e) {
            log.error("websocket IO异常");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("websocket 异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketRoomList.get(currentRoom).remove(this);    // 根据当前房间号，查找集合，删除
        subOnlineCount(); // 在线数减一

        log.info("有一连接关闭！[name=" + name + ", sid=" + sid + "]，当前在线人数为：" + getOnlineCount());

        try {
            // 当前退出的不是大厅而是房间的话，则判断房间是否还有连接，无连接则删除该房间
            if (!currentRoom.equals("0")) {
                List<WebSocketServer> webSocketServerList = webSocketRoomList.get(currentRoom);
                if (webSocketServerList.isEmpty()) {
                    redisUtil.del(currentRoom);
                    webSocketServerList.remove(currentRoom);
                }
            }
            sendInfo("[" + name + "]退出聊天!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自用户[" + name + "]的群发信息：" + message);

        // 群发消息
        try {
            userSendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    // 实现服务器主动推送
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    // 服务器群发自定义消息
    public void sendInfo(String message) throws IOException {
        log.info("推送消息到所有用户，推送内容：" + message);

        Map<String, Object> mapMsg = new HashMap<>();
        mapMsg.put("name", "系统消息(" + (currentRoom.equals("0") ? "大厅" : currentRoom) + ")");
        mapMsg.put("msg", message);
        mapMsg.put("type", 0);
        JSONObject jsonObj = new JSONObject(mapMsg);
        webSocketRoomList.get(currentRoom).forEach(item -> {
            try {
                // 自己不能给自己发消息...
//                if (item.sid.equals(this.sid)) {
//                    return;
//                }
                item.sendMessage(jsonObj.toString());
            } catch (IOException e) {
                return;
            }
        });
    }

    // 用户群发消息
    public void userSendMessage(String message) throws IOException {
        if (!StringUtils.isEmpty(message)) {
            Map<String, Object> mapMsg = new HashMap<>();
            mapMsg.put("name", name);
            mapMsg.put("msg", message);
            mapMsg.put("type", 2);
            JSONObject jsonObj = new JSONObject(mapMsg);
            webSocketRoomList.get(currentRoom).forEach(item -> {
                // 自己不能给自己发消息...
                if (item.sid.equals(this.sid)) {
                    return;
                }
                try {
                    item.sendMessage(jsonObj.toString());
                } catch (IOException e) {
                    return;
                }
            });
        }
    }
}
