package com.ruoyi.websocket.server;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zhangJiang
 * @Version： 1.0.0
 */

@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {
    private Session session;
    public static ConcurrentHashMap<String,Session> clients = new ConcurrentHashMap<>();

    //浏览器和服务器在建立连接的时候会调用此方法
    //建立关系
    @OnOpen
    public void onOpen(Session session, @PathParam( "token") String token){
        System.out.println("客户端:"+token+",建立连接");
        clients.put(token,session);
    }
    //浏览器和服务器之间断开连接之后会调用此方法.
    //移除关系
    @OnClose
    public void onClose(@PathParam( "token") String token){
        System.out.println("客户端:"+token+",断开连接");
        clients.remove(token);
    }
    //在服务器和浏览器通讯过程中出现异常会调用此方法
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }
}
