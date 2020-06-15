package com.sfwl.bh.config;

import com.google.gson.Gson;
import com.sfwl.bh.entity.DeviceInfo;
import com.sfwl.bh.enums.RedisKeyEnum;
import com.sfwl.bh.service.impl.BhWsService;
import com.sfwl.bh.utils.WsUtil;
import com.sfwl.bh.wshandler.BhWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/18 9:38
 */
@Slf4j
@Component
@EnableScheduling
public class BhSchedule implements InitializingBean, DisposableBean {

    @Autowired
    private BhWsService bhWsService;
    @Autowired
    private BhWebSocketHandler bhWebSocketHandler;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Gson gson = new Gson();

    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 1000)
    public void file() {
        Set<String> keys = redisTemplate.keys(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), "*", "*"));
        assert keys != null;
        for (String key : keys) {
            DeviceInfo deviceInfo = gson.fromJson(redisTemplate.opsForValue().get(key), DeviceInfo.class);
            bhWsService.sendMessage(deviceInfo.getDeviceId(), WsUtil.getFileData(deviceInfo.getBlockName()));
        }
    }

    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 1000)
    public void status() {
        Set<String> keys = redisTemplate.keys(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), "*", "*"));
        assert keys != null;
        for (String key : keys) {
            DeviceInfo deviceInfo = gson.fromJson(redisTemplate.opsForValue().get(key), DeviceInfo.class);
            bhWsService.sendMessage(deviceInfo.getDeviceId(), WsUtil.getStatusData(deviceInfo.getBlockName()));
        }
    }

    @Override
    @Deprecated
    public void afterPropertiesSet() throws Exception {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        try {
            client.execute(new URI("ws://newwuxian912.xicp.net:80"), bhWebSocketHandler)
                    .onTerminateDetach()
                    .doOnError(throwable -> log.info("异常!", throwable))
                    .subscribe(aVoid -> {
                    });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() throws Exception {
        Set<String> deviceInfoKeys = redisTemplate.keys(String.format(RedisKeyEnum.DEVICE_INFO.getKey(), "*", "*"));
        assert deviceInfoKeys != null;
        for (String deviceInfoKey : deviceInfoKeys) {
            DeviceInfo deviceInfo = gson.fromJson(redisTemplate.opsForValue().get(deviceInfoKey), DeviceInfo.class);
            Set<String> keys = redisTemplate.keys(String.format("DEVICE_%s_*", deviceInfo.getDeviceId()));
            assert keys != null;
            for (String key : keys) {
                // TODO
//                redisTemplate.delete(key);
            }
        }
    }
}
