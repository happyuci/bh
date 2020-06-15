package com.sfwl.bh.config;

import com.sfwl.bh.wshandler.BhWebSocketHandler;
import com.sfwl.bh.wshandler.DeviceInfoWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/14 16:43
 */
@Configuration
public class WsConfig {
    
    @Autowired
    private DeviceInfoWebSocketHandler deviceInfoWebSocketHandler;
    @Autowired
    private BhWebSocketHandler bhWebSocketHandler;

    @Bean
    public HandlerMapping handlerMapping() {
        // 对相应的URL进行添加处理器
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/device/infoWs", deviceInfoWebSocketHandler);
        map.put("/bh/ws", bhWebSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
