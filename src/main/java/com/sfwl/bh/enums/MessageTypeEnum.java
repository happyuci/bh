package com.sfwl.bh.enums;

public enum MessageTypeEnum {

    LOGIN(1, "登录验证码"),
    REGISTER(2, "注册验证码"),
    PWD_CHANGE(3, "找回密码"),
    ORIGIN_MOBILE(4, "原手机号验证"),
    NEW_MOBILE(5, "新手机号验证");

    private Integer type;
    private String desc;

    MessageTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
