package com.sfwl.bh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/12 9:53
 */
@Getter
@AllArgsConstructor
public enum RedisKeyEnum {
    MESSAGE_CODE_KEY("MESSAGE_CODE_%s_%s", "短信验证码 MESSAGE_CODE_{手机号}_{Type}"),

    DEVICE_INFO("DEVICE_%s_%s_INFO", "设备信息 DEVICE_{设备ID}_{模块}_INFO"),
    DEVICE_STATUS("DEVICE_%s_%s_STATUS", "设备状态 DEVICE_{设备ID}_{模块}_STATUS"),
    DEVICE_FILE("DEVICE_%s_%s_FILE", "设备状态 DEVICE_{设备ID}_{模块}_FILE"),

    DEVICE_BIND("DEVICE_%s_%s_BIND", "设备绑定 DEVICE_{设备ID}_{用户ID}_BIND"),

    DEVICE_MACHINE("DEVICE_%s_MACHINE", "设备机器 DEVICE_{设备ID}_MACHINE"),
    DEVICE_FILE_CONST("DEVICE_%s_FILE_CONST", "设备文件 DEVICE_{设备ID}_FILE_CONST"),
    DEVICE_STEP_CONST("DEVICE_%s_STEP_CONST", "设备步骤 DEVICE_{设备ID}_STEP_CONST"),
    ;
    private String key;
    private String desc;
}
