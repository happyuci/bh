package com.sfwl.bh.config;

import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/14 10:43
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<BaseModel<String>> handlerException(Exception e) {
        log.error("异常！", e);
        return Mono.just(new BaseModel<>(ResultStatus.ERROR));
    }
}
