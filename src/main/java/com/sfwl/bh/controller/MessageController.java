package com.sfwl.bh.controller;

import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.impl.MessageCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageCodeService messageCodeService;

    /**
     * 获取验证码
     */
    @GetMapping("/get")
    public Mono<BaseModel<String>> getMessageCode(@RequestParam("mobile") String mobile, @RequestParam("type") Integer type) {
        String messageCode = messageCodeService.get(mobile, type);
        return Mono.just(StringUtils.isNotBlank(messageCode) ? new BaseModel<>(ResultStatus.SUCCESS.getCode(), "发送成功!")
                : new BaseModel<>(ResultStatus.FAIL.getCode(), "发送失败!"));
    }

    /**
     * 校验验证码是否正确
     */
    @GetMapping("/check")
    public Mono<BaseModel<String>> checkMessageCode(@RequestParam("mobile") String mobile, @RequestParam("type") Integer type, @RequestParam("messageCode") String messageCode) {
        return Mono.just(messageCodeService.check(mobile, type, messageCode) ? new BaseModel<>(ResultStatus.SUCCESS.getCode(), "验证码正确!")
                : new BaseModel<>(ResultStatus.FAIL.getCode(), "验证码错误!"));
    }
}
