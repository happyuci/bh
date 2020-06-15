package com.sfwl.bh.wshandler;

import com.sfwl.bh.config.JWTUtil;
import com.sfwl.bh.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/14 16:37
 */
@Slf4j
@Component
public class DeviceInfoWebSocketHandler implements WebSocketHandler {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        Map<String, String> params = getQueryMap(handshakeInfo.getUri().getQuery());
        String authHeader = params.get("token");
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            return session.close(CloseStatus.TLS_HANDSHAKE_FAILURE);
        }
        if (!JWTUtil.verify(authHeader.substring(7))) {
            return session.close(CloseStatus.TLS_HANDSHAKE_FAILURE);
        }
        if (handshakeInfo.getUri().getQuery() == null) {
            return session.close(CloseStatus.REQUIRED_EXTENSION);
        }

        return session.send(
                Flux.interval(Duration.ofMillis(500))
                        .map(l -> String.format(RedisKeyEnum.DEVICE_STATUS.getKey(), params.get("deviceId"), params.get("blockName")))
                        .flatMap(reactiveRedisTemplate.opsForValue()::get)
                        .map(session::textMessage)
        );
    }

    private Map<String, String> getQueryMap(String queryStr) {
        Map<String, String> queryMap = new HashMap<>();
        if (!StringUtils.isEmpty(queryStr)) {
            String[] queryParam = queryStr.split("&");
            Arrays.stream(queryParam).forEach(s -> {
                String[] kv = s.split("=", 2);
                String value = kv.length == 2 ? kv[1] : "";
                queryMap.put(kv[0], value);
            });
        }
        return queryMap;
    }
}
