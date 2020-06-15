package com.sfwl.bh.service.impl;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;
import com.sfwl.bh.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MessageCodeService {

    @Autowired
    private SMSClient smsClient;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${jpush.message.tempId}")
    private Integer tempId;
    @Value("${jpush.message.signId}")
    private Integer signId;

    // 极光 - 发送单条模板短信 API
    private String sendMessageCode(String mobile) {
        String code = String.valueOf(new Random().nextInt(899999) + 1000);
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobildNumber(mobile)
                .setTempId(tempId)
                .setTTL(signId)
                .addTempPara("code", code)
                .build();
        try {
            SendSMSResult sendSmsResult = smsClient.sendTemplateSMS(payload);
            if (sendSmsResult != null && sendSmsResult.isResultOK()) {
                if (StringUtils.isNotBlank(sendSmsResult.getMessageId())) {
                    return code;
                }
            }
        } catch (APIConnectionException | APIRequestException e) {
            log.error("发送验证码失败!", e);
        }
        return null;
    }

    public String get(String mobile, int type) {
        String key = String.format(RedisKeyEnum.MESSAGE_CODE_KEY.getKey(), mobile, type);
        String messageCode = "123456"; // TODO
        redisTemplate.opsForValue().set(key, messageCode);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        return messageCode;
    }

    public boolean del(String mobile, int type) {
        String key = String.format(RedisKeyEnum.MESSAGE_CODE_KEY.getKey(), mobile, type);
        return redisTemplate.delete(key);
    }

    public boolean check(String mobile, int type, String messageCode) {
        String key = String.format(RedisKeyEnum.MESSAGE_CODE_KEY.getKey(), mobile, type);
        if (messageCode.equals(redisTemplate.opsForValue().get(key))) {
            return true;
        }
        return false;
    }
}


