package com.sfwl.bh;

import com.google.gson.Gson;
import com.sfwl.bh.service.impl.BhWsService;
import com.sfwl.bh.utils.WsUtil;
import com.sfwl.bh.wshandler.BhWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/18 9:38
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReactorWebSocketDemo {

    @Autowired
    private BhWsService bhWsService;
    @Autowired
    private BhWebSocketHandler bhWebSocketHandler;

    public void websocket() {
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

    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        websocket();
        // 等待连接完成
        TimeUnit.SECONDS.sleep(10);
        System.out.println(new Gson().toJson(bhWsService.getUnicastProcessorMap().keySet()));
        // 发送初始化数据
        String sessionId = bhWsService.getUnicastProcessorMap().keySet().iterator().next();
        bhWsService.sendMessage(sessionId, WsUtil.getInitData());
        TimeUnit.SECONDS.sleep(5);
        bhWsService.sendMessage(sessionId, WsUtil.getFileData("A"));
        TimeUnit.SECONDS.sleep(5);
        bhWsService.sendMessage(sessionId, WsUtil.getStatusData("A"));
        TimeUnit.SECONDS.sleep(5);

//        bhWsService.handleTerminate(bhWsService.getUnicastProcessorMap().keySet().iterator().next());
//        directProcessor.onNext("{\"FID\":\"003\",\"SUB\":\"OFEX.BTCPERP.Depth\"}");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
