package com.sq.sjc.ws;

import com.sq.system.common.utils.JsonUtil;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/sjc")
public class SjcRealtimeWebSocket {
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    public static void broadcast(String type, Object data) {
        String msg = JsonUtil.toJson(Map.of("type", type, "data", data, "ts", System.currentTimeMillis()));
        for (Session session : SESSIONS) {
            if (!session.isOpen()) continue;
            try {
                session.getBasicRemote().sendText(msg);
            } catch (Exception e) {
                log.warn("ws广播失败", e);
            }
        }
    }
}
