package com.sfwl.bh.wshandler;

import com.sfwl.bh.service.impl.BhWsService;
import com.sfwl.bh.utils.WsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.nio.charset.StandardCharsets;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/15 17:40
 */
@Slf4j
@Component
public class BhWebSocketHandler implements WebSocketHandler {

    @Autowired
    private BhWsService bhWsService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        UnicastProcessor<String> directProcessor = UnicastProcessor.create();
        Mono<Void> input = session.receive()
                .doOnSubscribe(subscription -> {
                    bhWsService.handleSubscribe(session.getId(), directProcessor);
                    bhWsService.sendMessage(session.getId(), WsUtil.getInitData());
                })
                .doOnNext(webSocketMessage -> {
                    bhWsService.handleMessage(session.getId(), webSocketMessage.getPayloadAsText(StandardCharsets.UTF_8));
                })
                .doOnError(throwable -> {
                    log.error("异常! {}", session.getId(), throwable);
                    bhWsService.handleTerminate(session.getId());
                })
                .doOnComplete(() -> log.info("结束! {}", session.getId()))
                .doOnTerminate(() -> {
                    log.info("连接断开! {}", session.getId());
                    bhWsService.handleTerminate(session.getId());
                })
                .then();

        Mono<Void> output = directProcessor.map(session::textMessage)
                .flatMap(webSocketMessage -> session.send(Mono.just(webSocketMessage)))
                .then();

//                Mono<Void> output = session.send(Flux.create(
//                        tFluxSink -> {
//                            directProcessor.doOnNext(msg -> {
//                                tFluxSink.next(session.textMessage(msg));
//                            }).doFinally((x) -> {
//                                tFluxSink.complete();
//                            }).subscribe();
//                        }));

        return Mono.zip(input, output).then()
                .doFinally(
                        signalType -> {
                            log.info("连接结束！{}", session.getId());
                        });
    }
}
